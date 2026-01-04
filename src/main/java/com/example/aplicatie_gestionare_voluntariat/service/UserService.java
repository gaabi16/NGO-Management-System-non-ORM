package com.example.aplicatie_gestionare_voluntariat.service;

import com.example.aplicatie_gestionare_voluntariat.dto.VolunteerRegistrationDto;
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

    @Transactional
    public String registerVolunteer(VolunteerRegistrationDto registrationDto) {
        // Creează user-ul
        User user = new User();
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setEmail(registrationDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));
        user.setPhoneNumber(registrationDto.getPhoneNumber());
        user.setRole(User.Role.volunteer);

        // Salvează utilizatorul
        User savedUser = userRepository.save(user);

        // Creează înregistrarea de volunteer cu toate detaliile
        Volunteer volunteer = new Volunteer();
        volunteer.setIdUser(savedUser.getIdUser());
        volunteer.setBirthDate(registrationDto.getBirthDate());
        volunteer.setSkills(registrationDto.getSkills());
        volunteer.setAvailability(registrationDto.getAvailability());
        volunteer.setEmergencyContact(registrationDto.getEmergencyContact());

        // Salvează datele de volunteer
        volunteerRepository.save(volunteer);

        return savedUser.getEmail();
    }

    // --- METODE NOI PENTRU PROFIL ---

    public VolunteerRegistrationDto getVolunteerProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Volunteer volunteer = volunteerRepository.findByUserId(user.getIdUser())
                .orElseThrow(() -> new RuntimeException("Volunteer details not found"));

        VolunteerRegistrationDto dto = new VolunteerRegistrationDto();
        // Date User
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());

        // Date Volunteer
        dto.setBirthDate(volunteer.getBirthDate());
        dto.setSkills(volunteer.getSkills());
        dto.setAvailability(volunteer.getAvailability());
        dto.setEmergencyContact(volunteer.getEmergencyContact());

        return dto;
    }

    @Transactional
    public void updateVolunteerProfile(String email, VolunteerRegistrationDto dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update User info
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        // Nu permitem schimbarea email-ului sau parolei în acest flux simplu momentan,
        // dar am putea adăuga logică dacă e necesar.
        userRepository.save(user);

        // Update Volunteer info
        Volunteer volunteer = volunteerRepository.findByUserId(user.getIdUser())
                .orElse(new Volunteer(user.getIdUser()));

        volunteer.setBirthDate(dto.getBirthDate());
        volunteer.setSkills(dto.getSkills());
        volunteer.setAvailability(dto.getAvailability());
        volunteer.setEmergencyContact(dto.getEmergencyContact());

        volunteerRepository.save(volunteer);
    }

    @Transactional
    public void deleteVolunteerAccount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Stergem intai dependintele (volunteers, activitati - prin constrangeri DB sau manual daca e nevoie)
        // În implementarea actuală, deleteByUserId din VolunteerRepository e suficient
        volunteerRepository.deleteByUserId(user.getIdUser());

        // Apoi stergem userul
        userRepository.deleteById(user.getIdUser());
    }
}