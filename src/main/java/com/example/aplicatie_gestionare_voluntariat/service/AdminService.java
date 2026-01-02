package com.example.aplicatie_gestionare_voluntariat.service;

import com.example.aplicatie_gestionare_voluntariat.model.Coordinator;
import com.example.aplicatie_gestionare_voluntariat.model.Ong;
import com.example.aplicatie_gestionare_voluntariat.model.User;
import com.example.aplicatie_gestionare_voluntariat.model.Volunteer;
import com.example.aplicatie_gestionare_voluntariat.repository.CoordinatorRepository;
import com.example.aplicatie_gestionare_voluntariat.repository.OngRepository;
import com.example.aplicatie_gestionare_voluntariat.repository.UserRepository;
import com.example.aplicatie_gestionare_voluntariat.repository.VolunteerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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
    private BCryptPasswordEncoder passwordEncoder;

    // ... (metodele existente getFirst5Ongs, PageWrapper, getUsersPage etc. rămân neschimbate) ...
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

    public PageWrapper<User> getUsersPage(int page, int size) {
        int offset = page * size;
        List<User> users = userRepository.findAllPaginated(offset, size);
        long totalElements = userRepository.count();
        return new PageWrapper<>(users, totalElements, page, size);
    }

    public PageWrapper<User> getUsersPageByRoles(int page, int size, List<User.Role> roles) {
        int offset = page * size;
        List<User> users;
        long totalElements;
        if (roles == null || roles.isEmpty()) {
            users = userRepository.findAllPaginated(offset, size);
            totalElements = userRepository.count();
        } else {
            users = userRepository.findByRoleInPaginated(roles, offset, size);
            totalElements = userRepository.countByRoleIn(roles);
        }
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

    // --- CRUD USERS ---

    @Transactional
    public User createUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        }
        User savedUser = userRepository.save(user);

        // La creare simplă, setăm un volunteer default (fără date extra) dacă rolul e volunteer
        if(savedUser.getRole() == User.Role.volunteer) {
            Volunteer volunteer = new Volunteer(savedUser.getIdUser());
            volunteerRepository.save(volunteer);
        }
        return savedUser;
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    public Coordinator getCoordinatorDetailsByUserId(Integer userId) {
        return coordinatorRepository.findByUserId(userId).orElse(null);
    }

    // NOU: Metoda pentru a lua detalii volunteer
    public Volunteer getVolunteerDetailsByUserId(Integer userId) {
        return volunteerRepository.findByUserId(userId).orElse(null);
    }

    @Transactional
    public User updateUser(Integer id, User updatedUser,
                           // Params Coordinator
                           Integer coordinatorOngId, String department, Integer experienceYears, String employmentType,
                           // Params Volunteer
                           LocalDate birthDate, String skills, String availability, String emergencyContact) {

        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser != null) {
            User.Role oldRole = existingUser.getRole();
            User.Role newRole = updatedUser.getRole();

            // 1. Update basic user info
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
            existingUser.setRole(newRole);

            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                existingUser.setPasswordHash(passwordEncoder.encode(updatedUser.getPassword()));
            }

            User savedUser = userRepository.save(existingUser);

            // 2. Curățare date vechi dacă s-a schimbat rolul
            if (oldRole != newRole) {
                if (oldRole == User.Role.volunteer) {
                    volunteerRepository.deleteByUserId(id);
                } else if (oldRole == User.Role.coordinator) {
                    coordinatorRepository.deleteByUserId(id);
                }
            }

            // 3. Salvare date noi în funcție de noul rol
            if (newRole == User.Role.coordinator) {
                // Dacă avem datele minime necesare
                if (coordinatorOngId != null) {
                    Coordinator coordinator = new Coordinator();
                    User u = new User(); u.setIdUser(id);
                    coordinator.setUser(u);
                    Ong o = new Ong(); o.setIdOng(coordinatorOngId);
                    coordinator.setOng(o);
                    coordinator.setDepartment(department);
                    coordinator.setExperienceYears(experienceYears);
                    coordinator.setEmploymentType(employmentType);
                    coordinatorRepository.save(coordinator);
                }
            } else if (newRole == User.Role.volunteer) {
                // Salvăm datele extra pentru volunteer
                Volunteer volunteer = new Volunteer(id);
                volunteer.setBirthDate(birthDate);
                volunteer.setSkills(skills);
                volunteer.setAvailability(availability);
                volunteer.setEmergencyContact(emergencyContact);
                volunteerRepository.save(volunteer);
            }

            return savedUser;
        }
        return null;
    }

    @Transactional
    public boolean deleteUser(Integer id, String currentUserEmail) {
        User userToDelete = userRepository.findById(id).orElse(null);
        if (userToDelete == null) return false;

        if (userToDelete.getRole() == User.Role.admin) {
            throw new IllegalStateException("Admin accounts cannot be deleted for security reasons!");
        }

        // Curățăm tabelele dependente
        volunteerRepository.deleteByUserId(id);
        coordinatorRepository.deleteByUserId(id);

        userRepository.deleteById(id);
        return true;
    }

    // ... (metodele createOng, updateOng, deleteOng rămân neschimbate) ...
    @Transactional
    public Ong createOng(Ong ong) { return ongRepository.save(ong); }
    public Ong getOngById(Integer id) { return ongRepository.findById(id).orElse(null); }
    @Transactional
    public Ong updateOng(Integer id, Ong updatedOng) {
        Ong existingOng = ongRepository.findById(id).orElse(null);
        if (existingOng != null) {
            existingOng.setName(updatedOng.getName());
            existingOng.setDescription(updatedOng.getDescription());
            existingOng.setAddress(updatedOng.getAddress());
            existingOng.setRegistrationNumber(updatedOng.getRegistrationNumber());
            existingOng.setPhone(updatedOng.getPhone());
            existingOng.setEmail(updatedOng.getEmail());
            existingOng.setFoundingDate(updatedOng.getFoundingDate());
            return ongRepository.save(existingOng);
        }
        return null;
    }
    @Transactional
    public boolean deleteOng(Integer id) {
        Ong ongToDelete = ongRepository.findById(id).orElse(null);
        if (ongToDelete == null) return false;
        ongRepository.deleteById(id);
        return true;
    }
}