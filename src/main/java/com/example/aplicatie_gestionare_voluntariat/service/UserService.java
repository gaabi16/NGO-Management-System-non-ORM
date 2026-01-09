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
        if(user.getPassword() != null) {
            user.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        }
        if(user.getRole() == null) {
            user.setRole(User.Role.volunteer);
        }
        User savedUser = userRepository.save(user);

        if(savedUser.getRole() == User.Role.volunteer) {
            Volunteer volunteer = new Volunteer(savedUser.getIdUser());
            volunteerRepository.save(volunteer);
        }
        return savedUser;
    }

    @Transactional
    public String registerVolunteer(VolunteerRegistrationDto registrationDto) {
        // VALIDARE BACKEND (Conform create_tables.sql)
        if (registrationDto.getFirstName() == null || registrationDto.getFirstName().trim().isEmpty()) throw new IllegalArgumentException("First Name is required.");
        if (registrationDto.getLastName() == null || registrationDto.getLastName().trim().isEmpty()) throw new IllegalArgumentException("Last Name is required.");
        if (registrationDto.getEmail() == null || registrationDto.getEmail().trim().isEmpty()) throw new IllegalArgumentException("Email is required.");
        if (registrationDto.getPassword() == null || registrationDto.getPassword().trim().isEmpty()) throw new IllegalArgumentException("Password is required.");

        // Phone number este NULLABLE in baza de date, deci nu il validam strict.

        if (registrationDto.getBirthDate() == null) throw new IllegalArgumentException("Birth Date is required.");
        if (registrationDto.getSkills() == null || registrationDto.getSkills().trim().isEmpty()) throw new IllegalArgumentException("Skills are required.");
        if (registrationDto.getAvailability() == null || registrationDto.getAvailability().trim().isEmpty()) throw new IllegalArgumentException("Availability is required.");

        // Emergency Contact este NULLABLE in baza de date (conform create_tables.sql modificat), deci este optional.

        // Logica de salvare
        User user = new User();
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setEmail(registrationDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));
        user.setPhoneNumber(registrationDto.getPhoneNumber());
        user.setRole(User.Role.volunteer);

        User savedUser = userRepository.save(user);

        Volunteer volunteer = new Volunteer();
        volunteer.setIdUser(savedUser.getIdUser());
        volunteer.setBirthDate(registrationDto.getBirthDate());
        volunteer.setSkills(registrationDto.getSkills());
        volunteer.setAvailability(registrationDto.getAvailability());
        volunteer.setEmergencyContact(registrationDto.getEmergencyContact());

        volunteerRepository.save(volunteer);

        return savedUser.getEmail();
    }

    public VolunteerRegistrationDto getVolunteerProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Volunteer volunteer = volunteerRepository.findByUserId(user.getIdUser())
                .orElseThrow(() -> new RuntimeException("Volunteer details not found"));

        VolunteerRegistrationDto dto = new VolunteerRegistrationDto();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
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

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        userRepository.save(user);

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
        volunteerRepository.deleteByUserId(user.getIdUser());
        userRepository.deleteById(user.getIdUser());
    }
}