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

    public List<Ong> getFirst5Ongs() {
        return ongRepository.findFirst5();
    }

    public List<Coordinator> getFirst5Coordinators() {
        return coordinatorRepository.findFirst5();
    }

    // Clasa wrapper pentru paginare
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

    // NOU: Metoda pentru paginarea ONG-urilor
    public PageWrapper<Ong> getOngsPage(int page, int size) {
        int offset = page * size;
        List<Ong> ongs = ongRepository.findAll(size, offset); // Atenție: repository-ul are (limit, offset)
        long totalElements = ongRepository.count();
        return new PageWrapper<>(ongs, totalElements, page, size);
    }

    // CRUD pentru Users
    @Transactional
    public User createUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        }

        User savedUser = userRepository.save(user);

        // Dacă rolul este volunteer, creează și înregistrarea în tabelul volunteers
        if(savedUser.getRole() == User.Role.volunteer) {
            Volunteer volunteer = new Volunteer(savedUser.getIdUser());
            volunteerRepository.save(volunteer);
        }

        return savedUser;
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public User updateUser(Integer id, User updatedUser) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser != null) {
            User.Role oldRole = existingUser.getRole();
            User.Role newRole = updatedUser.getRole();

            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
            existingUser.setRole(newRole);

            // Actualizează parola doar dacă este furnizată
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                existingUser.setPasswordHash(passwordEncoder.encode(updatedUser.getPassword()));
            }

            User savedUser = userRepository.save(existingUser);

            // Gestionează schimbarea rolului
            if (oldRole != newRole) {
                // Dacă s-a schimbat de la volunteer la altceva, șterge din volunteers
                if (oldRole == User.Role.volunteer && newRole != User.Role.volunteer) {
                    volunteerRepository.deleteByUserId(id);
                }

                // Dacă s-a schimbat la volunteer, adaugă în volunteers
                if (newRole == User.Role.volunteer && oldRole != User.Role.volunteer) {
                    Volunteer volunteer = new Volunteer(savedUser.getIdUser());
                    volunteerRepository.save(volunteer);
                }
            }

            return savedUser;
        }
        return null;
    }

    @Transactional
    public boolean deleteUser(Integer id, String currentUserEmail) {
        User userToDelete = userRepository.findById(id).orElse(null);
        if (userToDelete == null) {
            return false;
        }

        // Verifică dacă utilizatorul de șters este admin
        if (userToDelete.getRole() == User.Role.admin) {
            throw new IllegalStateException("Admin accounts cannot be deleted for security reasons!");
        }

        // Șterge mai întâi din tabelul volunteers dacă este volunteer
        if (userToDelete.getRole() == User.Role.volunteer) {
            volunteerRepository.deleteByUserId(id);
        }

        // Apoi șterge utilizatorul
        userRepository.deleteById(id);
        return true;
    }
}