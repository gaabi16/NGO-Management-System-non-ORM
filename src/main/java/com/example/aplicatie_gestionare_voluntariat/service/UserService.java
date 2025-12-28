package com.example.aplicatie_gestionare_voluntariat.service;

import com.example.aplicatie_gestionare_voluntariat.model.User;
import com.example.aplicatie_gestionare_voluntariat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        // criptează parola din câmpul temporar
        if(user.getPassword() != null) {
            user.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        }
        // setează rol default
        if(user.getRole() == null) {
            user.setRole(User.Role.volunteer);
        }
        return userRepository.save(user);
    }
}