package com.example.aplicatie_gestionare_voluntariat.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String home(Authentication authentication) {
        if (authentication != null) {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_admin"))) {
                return "redirect:/admin/dashboard";
            }
            // [NOU] Redirecționare pentru volunteer
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_volunteer"))) {
                return "redirect:/volunteer/dashboard";
            }
        }
        return "index";
    }
}