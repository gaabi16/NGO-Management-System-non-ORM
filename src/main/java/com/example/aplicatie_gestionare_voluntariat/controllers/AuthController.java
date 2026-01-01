package com.example.aplicatie_gestionare_voluntariat.controllers;

import com.example.aplicatie_gestionare_voluntariat.dto.VolunteerRegistrationDto;
import com.example.aplicatie_gestionare_voluntariat.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private SecurityContextRepository securityContextRepository;

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("volunteerRegistration", new VolunteerRegistrationDto());
        return "signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(@ModelAttribute("volunteerRegistration") VolunteerRegistrationDto registrationDto,
                               HttpServletRequest request,
                               HttpServletResponse response) {
        // Salvează parola înainte de criptare pentru autologin
        String rawPassword = registrationDto.getPassword();

        // Înregistrează utilizatorul cu toate datele de volunteer
        String registeredEmail = userService.registerVolunteer(registrationDto);

        // Autentifică automat utilizatorul după înregistrare
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(registeredEmail, rawPassword);

        Authentication authentication = authenticationManager.authenticate(authToken);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        securityContextRepository.saveContext(securityContext, request, response);

        return "redirect:/home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}