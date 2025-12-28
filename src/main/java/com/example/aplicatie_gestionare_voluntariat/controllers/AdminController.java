package com.example.aplicatie_gestionare_voluntariat.controllers;

import com.example.aplicatie_gestionare_voluntariat.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard")
    public String adminDashboard(
            @RequestParam(required = false) String view,
            Model model,
            Authentication authentication) {

        // Verifică dacă utilizatorul este admin
        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_admin"))) {
            return "redirect:/home";
        }

        model.addAttribute("currentView", view);

        // Încarcă datele în funcție de parametrul view
        if ("users".equals(view)) {
            model.addAttribute("users", adminService.getFirst5Users());
        } else if ("ongs".equals(view)) {
            model.addAttribute("ongs", adminService.getFirst5Ongs());
        } else if ("coordinators".equals(view)) {
            model.addAttribute("coordinators", adminService.getFirst5Coordinators());
        }

        return "admin-dashboard";
    }
}