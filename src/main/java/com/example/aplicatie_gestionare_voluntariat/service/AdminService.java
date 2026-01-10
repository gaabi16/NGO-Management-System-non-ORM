package com.example.aplicatie_gestionare_voluntariat.service;

import com.example.aplicatie_gestionare_voluntariat.model.Coordinator;
import com.example.aplicatie_gestionare_voluntariat.model.Ong;
import com.example.aplicatie_gestionare_voluntariat.model.User;
import com.example.aplicatie_gestionare_voluntariat.model.Volunteer;
import com.example.aplicatie_gestionare_voluntariat.repository.ActivityRepository;
import com.example.aplicatie_gestionare_voluntariat.repository.CoordinatorRepository;
import com.example.aplicatie_gestionare_voluntariat.repository.OngRepository;
import com.example.aplicatie_gestionare_voluntariat.repository.UserRepository;
import com.example.aplicatie_gestionare_voluntariat.repository.VolunteerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OngRepository ongRepository;
    @Autowired
    private CoordinatorRepository coordinatorRepository;
    @Autowired
    private VolunteerRepository volunteerRepository;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Regex Validare
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    private boolean isValidEmail(String email) {
        return email != null && Pattern.matches(EMAIL_PATTERN, email);
    }

    public List<Ong> getFirst5Ongs() { return ongRepository.findFirst5(); }
    public List<Coordinator> getFirst5Coordinators() { return coordinatorRepository.findFirst5(); }
    public List<Ong> getAllOngs() { return ongRepository.findAll(); }

    public static class PageWrapper<T> {
        private List<T> content;
        private int totalPages;
        private long totalElements;
        private int currentPage;
        public PageWrapper(List<T> content, long totalElements, int currentPage, int size) {
            this.content = content;
            this.totalElements = totalElements;
            this.currentPage = currentPage;
            this.totalPages = (int) Math.ceil((double) totalElements / size);
        }
        public List<T> getContent() { return content; }
        public int getTotalPages() { return totalPages; }
        public long getTotalElements() { return totalElements; }
        public int getNumber() { return currentPage; }
    }

    // [MODIFICAT] Metoda unificata care suporta search + roluri
    public PageWrapper<User> getUsersPageFiltered(int page, int size, String search, List<User.Role> roles) {
        int offset = page * size;
        List<User> users = userRepository.findFilteredUsers(search, roles, offset, size);
        long totalElements = userRepository.countFilteredUsers(search, roles);
        return new PageWrapper<>(users, totalElements, page, size);
    }

    public PageWrapper<Ong> getOngsPage(int page, int size, String search) {
        int offset = page * size;
        List<Ong> ongs;
        long totalElements;
        if (search != null && !search.trim().isEmpty()) {
            ongs = ongRepository.findByRegistrationNumberPaginated(search.trim(), size, offset);
            totalElements = ongRepository.countByRegistrationNumber(search.trim());
        } else {
            ongs = ongRepository.findAll(size, offset);
            totalElements = ongRepository.count();
        }
        return new PageWrapper<>(ongs, totalElements, page, size);
    }

    public Map<String, Object> getSystemStatistics() {
        Map<String, Object> stats = new HashMap<>();
        Map<String, Object> topVol = volunteerRepository.findMostActiveVolunteer();
        stats.put("topVolunteerName", topVol != null ? topVol.get("name") : "N/A");
        stats.put("topVolunteerCount", topVol != null ? topVol.get("count") : 0);
        Map<String, Object> topOng = ongRepository.findTopFundraisingOng();
        stats.put("topOngName", topOng != null ? topOng.get("name") : "N/A");
        stats.put("topOngDonations", topOng != null ? String.format("%.2f", topOng.get("total")) : "0.00");
        Map<String, Object> topCoord = coordinatorRepository.findTopCoordinator();
        stats.put("topCoordinatorName", topCoord != null ? topCoord.get("name") : "N/A");
        stats.put("topCoordinatorCount", topCoord != null ? topCoord.get("count") : 0);
        Map<String, Object> popAct = activityRepository.findMostPopularActivity();
        stats.put("popularActivityName", popAct != null ? popAct.get("name") : "N/A");
        stats.put("popularActivityCount", popAct != null ? popAct.get("count") : 0);
        Double totalDonations = activityRepository.getTotalSystemDonations();
        stats.put("totalSystemDonations", String.format("%.2f", totalDonations));
        stats.put("totalVolunteers", volunteerRepository.count());
        stats.put("totalCoordinators", coordinatorRepository.count());
        return stats;
    }

    @Transactional
    public User createUser(User user,
                           String coordinatorOngRegNumber, String department, Integer experienceYears, String employmentType,
                           LocalDate birthDate, String skills, String availability, String emergencyContact) {
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) throw new IllegalArgumentException("First Name is required");
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) throw new IllegalArgumentException("Last Name is required");

        // VALIDARE EMAIL
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) throw new IllegalArgumentException("Email is required");
        if (!isValidEmail(user.getEmail())) throw new IllegalArgumentException("Invalid email format (example: user@domain.com)");

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) throw new IllegalArgumentException("Password is required");

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        }

        User savedUser = userRepository.save(user);

        if (savedUser.getRole() == User.Role.volunteer) {
            if (birthDate == null) throw new IllegalArgumentException("Birth Date is required for Volunteers");
            if (skills == null || skills.trim().isEmpty()) throw new IllegalArgumentException("Skills are required for Volunteers");
            if (availability == null || availability.trim().isEmpty()) throw new IllegalArgumentException("Availability is required for Volunteers");

            Volunteer volunteer = new Volunteer(savedUser.getIdUser());
            volunteer.setBirthDate(birthDate);
            volunteer.setSkills(skills);
            volunteer.setAvailability(availability);
            volunteer.setEmergencyContact(emergencyContact);
            volunteerRepository.save(volunteer);

        } else if (savedUser.getRole() == User.Role.coordinator) {
            if (coordinatorOngRegNumber == null || coordinatorOngRegNumber.trim().isEmpty()) throw new IllegalArgumentException("ONG Selection is required for Coordinators");
            if (department == null || department.trim().isEmpty()) throw new IllegalArgumentException("Department is required for Coordinators");
            if (experienceYears == null) throw new IllegalArgumentException("Experience Years is required for Coordinators");
            if (employmentType == null || employmentType.trim().isEmpty()) throw new IllegalArgumentException("Employment Type is required for Coordinators");

            Coordinator coordinator = new Coordinator();
            User u = new User(); u.setIdUser(savedUser.getIdUser());
            coordinator.setUser(u);
            coordinator.setOngRegistrationNumber(coordinatorOngRegNumber);
            coordinator.setDepartment(department);
            coordinator.setExperienceYears(experienceYears);
            coordinator.setEmploymentType(employmentType);

            coordinatorRepository.save(coordinator);
        }

        return savedUser;
    }

    public User getUserById(Integer id) { return userRepository.findById(id).orElse(null); }
    public Coordinator getCoordinatorDetailsByUserId(Integer userId) { return coordinatorRepository.findByUserId(userId).orElse(null); }
    public Volunteer getVolunteerDetailsByUserId(Integer userId) { return volunteerRepository.findByUserId(userId).orElse(null); }

    public boolean hasVolunteerConflicts(Integer userId) {
        return volunteerRepository.hasActiveOrPendingActivities(userId);
    }

    @Transactional
    public User updateUser(Integer id, User updatedUser,
                           String coordinatorOngRegNumber, String department, Integer experienceYears, String employmentType,
                           LocalDate birthDate, String skills, String availability, String emergencyContact) {
        if (updatedUser.getFirstName() == null || updatedUser.getFirstName().trim().isEmpty()) throw new IllegalArgumentException("First Name cannot be empty");
        if (updatedUser.getLastName() == null || updatedUser.getLastName().trim().isEmpty()) throw new IllegalArgumentException("Last Name cannot be empty");

        // VALIDARE EMAIL
        if (updatedUser.getEmail() == null || updatedUser.getEmail().trim().isEmpty()) throw new IllegalArgumentException("Email cannot be empty");
        if (!isValidEmail(updatedUser.getEmail())) throw new IllegalArgumentException("Invalid email format (example: user@domain.com)");

        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser != null) {
            User.Role oldRole = existingUser.getRole();
            User.Role newRole = updatedUser.getRole();

            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
            existingUser.setRole(newRole);

            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                existingUser.setPasswordHash(passwordEncoder.encode(updatedUser.getPassword()));
            }

            User savedUser = userRepository.save(existingUser);

            if (oldRole != newRole) {
                if (oldRole == User.Role.volunteer) {
                    volunteerRepository.deleteActivitiesByUserId(id);
                    volunteerRepository.deleteByUserId(id);
                }
                else if (oldRole == User.Role.coordinator) {
                    Optional<Coordinator> c = coordinatorRepository.findByUserId(id);
                    if(c.isPresent()) {
                        Integer coordId = c.get().getIdCoordinator();
                        jdbcTemplate.update("DELETE FROM volunteer_activities WHERE id_activity IN (SELECT id_activity FROM activities WHERE id_coordinator = ?)", coordId);
                        jdbcTemplate.update("DELETE FROM activities WHERE id_coordinator = ?", coordId);
                    }
                    coordinatorRepository.deleteByUserId(id);
                }
            }

            if (newRole == User.Role.coordinator) {
                if (coordinatorOngRegNumber == null || coordinatorOngRegNumber.trim().isEmpty()) throw new IllegalArgumentException("ONG required for Coordinator");
                if (department == null || department.trim().isEmpty()) throw new IllegalArgumentException("Department required for Coordinator");
                if (experienceYears == null) throw new IllegalArgumentException("Experience Years required for Coordinator");
                if (employmentType == null || employmentType.trim().isEmpty()) throw new IllegalArgumentException("Employment Type required for Coordinator");

                Optional<Coordinator> existingCoord = coordinatorRepository.findByUserId(id);
                Coordinator coordinator = existingCoord.orElse(new Coordinator());

                if (existingCoord.isEmpty()) {
                    User u = new User(); u.setIdUser(id);
                    coordinator.setUser(u);
                }

                coordinator.setOngRegistrationNumber(coordinatorOngRegNumber);
                coordinator.setDepartment(department);
                coordinator.setExperienceYears(experienceYears);
                coordinator.setEmploymentType(employmentType);
                coordinatorRepository.save(coordinator);

            } else if (newRole == User.Role.volunteer) {
                if (birthDate == null) throw new IllegalArgumentException("Birth Date required for Volunteer");
                if (skills == null || skills.trim().isEmpty()) throw new IllegalArgumentException("Skills required for Volunteer");
                if (availability == null || availability.trim().isEmpty()) throw new IllegalArgumentException("Availability required for Volunteer");

                Optional<Volunteer> existingVol = volunteerRepository.findByUserId(id);
                Volunteer volunteer = existingVol.orElse(new Volunteer(id));

                volunteer.setBirthDate(birthDate);
                volunteer.setSkills(skills);
                volunteer.setAvailability(availability);
                volunteer.setEmergencyContact(emergencyContact);
                volunteerRepository.save(volunteer);
            }
            return savedUser;
        }
        throw new IllegalArgumentException("User not found");
    }

    @Transactional
    public boolean deleteUser(Integer id, String currentUserEmail) {
        User userToDelete = userRepository.findById(id).orElse(null);
        if (userToDelete == null) return false;
        if (userToDelete.getRole() == User.Role.admin) throw new IllegalStateException("Admin accounts cannot be deleted!");

        volunteerRepository.deleteActivitiesByUserId(id);
        volunteerRepository.deleteByUserId(id);

        Optional<Coordinator> coord = coordinatorRepository.findByUserId(id);
        if (coord.isPresent()) {
            Integer coordId = coord.get().getIdCoordinator();
            jdbcTemplate.update("DELETE FROM volunteer_activities WHERE id_activity IN (SELECT id_activity FROM activities WHERE id_coordinator = ?)", coordId);
            jdbcTemplate.update("DELETE FROM activities WHERE id_coordinator = ?", coordId);
            coordinatorRepository.deleteByUserId(id);
        }

        userRepository.deleteById(id);
        return true;
    }

    @Transactional
    public Ong createOng(Ong ong) {
        if (ong.getRegistrationNumber() == null || ong.getRegistrationNumber().trim().isEmpty()) throw new IllegalArgumentException("Registration Number is required");
        if (ong.getName() == null || ong.getName().trim().isEmpty()) throw new IllegalArgumentException("Name is required");
        if (ong.getDescription() == null || ong.getDescription().trim().isEmpty()) throw new IllegalArgumentException("Description is required");
        if (ong.getAddress() == null || ong.getAddress().trim().isEmpty()) throw new IllegalArgumentException("Address is required");
        if (ong.getCountry() == null || ong.getCountry().trim().isEmpty()) throw new IllegalArgumentException("Country is required");
        if (ong.getPhone() == null || ong.getPhone().trim().isEmpty()) throw new IllegalArgumentException("Phone is required");

        // VALIDARE EMAIL ONG
        if (ong.getEmail() == null || ong.getEmail().trim().isEmpty()) throw new IllegalArgumentException("Email is required");
        if (!isValidEmail(ong.getEmail())) throw new IllegalArgumentException("Invalid email format (example: contact@ong.com)");

        Optional<Ong> existingOng = ongRepository.findById(ong.getRegistrationNumber());
        if (existingOng.isPresent()) {
            throw new IllegalArgumentException("An ONG with Registration Number '" + ong.getRegistrationNumber() + "' already exists!");
        }
        return ongRepository.save(ong);
    }

    public Ong getOngById(String id) { return ongRepository.findById(id).orElse(null); }

    @Transactional
    public Ong updateOng(String registrationNumber, Ong updatedOng) {
        if (updatedOng.getName() == null || updatedOng.getName().trim().isEmpty()) throw new IllegalArgumentException("Name cannot be empty");
        if (updatedOng.getDescription() == null || updatedOng.getDescription().trim().isEmpty()) throw new IllegalArgumentException("Description cannot be empty");
        if (updatedOng.getAddress() == null || updatedOng.getAddress().trim().isEmpty()) throw new IllegalArgumentException("Address cannot be empty");
        if (updatedOng.getCountry() == null || updatedOng.getCountry().trim().isEmpty()) throw new IllegalArgumentException("Country cannot be empty");
        if (updatedOng.getPhone() == null || updatedOng.getPhone().trim().isEmpty()) throw new IllegalArgumentException("Phone cannot be empty");

        // VALIDARE EMAIL ONG UPDATE
        if (updatedOng.getEmail() == null || updatedOng.getEmail().trim().isEmpty()) throw new IllegalArgumentException("Email cannot be empty");
        if (!isValidEmail(updatedOng.getEmail())) throw new IllegalArgumentException("Invalid email format (example: contact@ong.com)");

        Ong existingOng = ongRepository.findById(registrationNumber).orElse(null);
        if (existingOng != null) {
            existingOng.setName(updatedOng.getName());
            existingOng.setDescription(updatedOng.getDescription());
            existingOng.setAddress(updatedOng.getAddress());
            existingOng.setCountry(updatedOng.getCountry());
            existingOng.setPhone(updatedOng.getPhone());
            existingOng.setEmail(updatedOng.getEmail());
            existingOng.setFoundingDate(updatedOng.getFoundingDate());
            return ongRepository.save(existingOng);
        }
        throw new IllegalArgumentException("ONG not found");
    }

    @Transactional
    public boolean deleteOng(String registrationNumber) {
        Ong ong = ongRepository.findById(registrationNumber).orElse(null);
        if (ong == null) return false;

        String sqlGetCoordinators = "SELECT id_user FROM coordinators WHERE ong_registration_number = ?";
        List<Integer> coordinatorUserIds = jdbcTemplate.queryForList(sqlGetCoordinators, Integer.class, registrationNumber);

        for (Integer userId : coordinatorUserIds) {
            String sqlGetCoordId = "SELECT id_coordinator FROM coordinators WHERE id_user = ?";
            Integer coordinatorId = jdbcTemplate.queryForObject(sqlGetCoordId, Integer.class, userId);

            if (coordinatorId != null) {
                String sqlDeleteEnrollments = "DELETE FROM volunteer_activities WHERE id_activity IN (SELECT id_activity FROM activities WHERE id_coordinator = ?)";
                jdbcTemplate.update(sqlDeleteEnrollments, coordinatorId);

                String sqlDeleteActivities = "DELETE FROM activities WHERE id_coordinator = ?";
                jdbcTemplate.update(sqlDeleteActivities, coordinatorId);
            }
            String sqlDeleteCoord = "DELETE FROM coordinators WHERE id_user = ?";
            jdbcTemplate.update(sqlDeleteCoord, userId);
            userRepository.deleteById(userId);
        }
        ongRepository.deleteById(registrationNumber);
        return true;
    }
}