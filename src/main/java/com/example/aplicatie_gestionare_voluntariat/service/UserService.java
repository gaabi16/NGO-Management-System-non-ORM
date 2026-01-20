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

import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VolunteerRepository volunteerRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    private boolean isValidEmail(String email) {
        return email != null && Pattern.matches(EMAIL_PATTERN, email);
    }

    @Transactional
    public User registerUser(User user) {
        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format (example: user@domain.com)");
        }
        if(user.getPassword() != null) user.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        if(user.getRole() == null) user.setRole(User.Role.volunteer);
        User savedUser = userRepository.save(user);
        if(savedUser.getRole() == User.Role.volunteer) { Volunteer volunteer = new Volunteer(savedUser.getIdUser()); volunteerRepository.save(volunteer); }
        return savedUser;
    }

    @Transactional
    public String registerVolunteer(VolunteerRegistrationDto registrationDto) {
        if (registrationDto.getFirstName() == null || registrationDto.getFirstName().trim().isEmpty()) throw new IllegalArgumentException("First Name is required.");
        if (registrationDto.getLastName() == null || registrationDto.getLastName().trim().isEmpty()) throw new IllegalArgumentException("Last Name is required.");

        if (registrationDto.getEmail() == null || registrationDto.getEmail().trim().isEmpty()) throw new IllegalArgumentException("Email is required.");
        if (!isValidEmail(registrationDto.getEmail())) throw new IllegalArgumentException("Invalid email format. Must be like: user@domain.com");

        if (registrationDto.getPassword() == null || registrationDto.getPassword().trim().isEmpty()) throw new IllegalArgumentException("Password is required.");
        if (registrationDto.getBirthDate() == null) throw new IllegalArgumentException("Birth Date is required.");
        if (registrationDto.getSkills() == null || registrationDto.getSkills().trim().isEmpty()) throw new IllegalArgumentException("Skills are required.");
        if (registrationDto.getAvailability() == null || registrationDto.getAvailability().trim().isEmpty()) throw new IllegalArgumentException("Availability is required.");

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
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Volunteer volunteer = volunteerRepository.findByUserId(user.getIdUser()).orElseThrow(() -> new RuntimeException("Volunteer details not found"));
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
        if (dto.getFirstName() == null || dto.getFirstName().trim().isEmpty()) throw new IllegalArgumentException("First Name cannot be empty");
        if (dto.getLastName() == null || dto.getLastName().trim().isEmpty()) throw new IllegalArgumentException("Last Name cannot be empty");
        if (dto.getBirthDate() == null) throw new IllegalArgumentException("Birth Date cannot be empty");
        if (dto.getSkills() == null || dto.getSkills().trim().isEmpty()) throw new IllegalArgumentException("Skills cannot be empty");
        if (dto.getAvailability() == null || dto.getAvailability().trim().isEmpty()) throw new IllegalArgumentException("Availability cannot be empty");

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        userRepository.save(user);

        Volunteer volunteer = volunteerRepository.findByUserId(user.getIdUser()).orElse(new Volunteer(user.getIdUser()));
        volunteer.setBirthDate(dto.getBirthDate());
        volunteer.setSkills(dto.getSkills());
        volunteer.setAvailability(dto.getAvailability());
        volunteer.setEmergencyContact(dto.getEmergencyContact());
        volunteerRepository.save(volunteer);
    }

    @Transactional
    public void deleteVolunteerAccount(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        volunteerRepository.deleteByUserId(user.getIdUser());
        userRepository.deleteById(user.getIdUser());
    }
}