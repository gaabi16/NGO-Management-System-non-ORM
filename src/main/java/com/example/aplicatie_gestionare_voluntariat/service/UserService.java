package com.example.aplicatie_gestionare_voluntariat.service;

import com.example.aplicatie_gestionare_voluntariat.model.User;
import com.example.aplicatie_gestionare_voluntariat.model.Volunteer;
import com.example.aplicatie_gestionare_voluntariat.repository.UserRepository;
import com.example.aplicatie_gestionare_voluntariat.repository.VolunteerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VolunteerRepository volunteerRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(User user) {
        // Criptează parola din câmpul temporar
        if(user.getPassword() != null) {
            user.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        }

        // Setează rol default
        if(user.getRole() == null) {
            user.setRole(User.Role.volunteer);
        }

        // Salvează utilizatorul
        User savedUser = userRepository.save(user);

        // Dacă rolul este volunteer, creează și înregistrarea în tabelul volunteers
        if(savedUser.getRole() == User.Role.volunteer) {
            Volunteer volunteer = new Volunteer(savedUser.getIdUser());
            volunteerRepository.save(volunteer);
        }

        return savedUser;
    }
}