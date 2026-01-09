package com.example.aplicatie_gestionare_voluntariat.controllers;

import com.example.aplicatie_gestionare_voluntariat.model.Coordinator;
import com.example.aplicatie_gestionare_voluntariat.model.Ong;
import com.example.aplicatie_gestionare_voluntariat.model.User;
import com.example.aplicatie_gestionare_voluntariat.model.Volunteer;
import com.example.aplicatie_gestionare_voluntariat.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard")
    public String adminDashboard(
            @RequestParam(required = false) String view,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) List<String> roles,
            @RequestParam(required = false) String search,
            Model model,
            Authentication authentication) {

        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_admin"))) {
            return "redirect:/home";
        }

        model.addAttribute("currentView", view);
        model.addAttribute("currentUserEmail", authentication.getName());
        model.addAttribute("allOngs", adminService.getAllOngs());

        if ("users".equals(view)) {
            // [MODIFICARE] 50 de useri pe pagină
            int pageSize = 50;
            AdminService.PageWrapper<User> usersPage;
            if (roles != null && !roles.isEmpty()) {
                List<User.Role> roleEnums = roles.stream().map(User.Role::valueOf).collect(Collectors.toList());
                usersPage = adminService.getUsersPageByRoles(page, pageSize, roleEnums);
            } else {
                usersPage = adminService.getUsersPage(page, pageSize);
            }
            model.addAttribute("usersPage", usersPage);
            model.addAttribute("currentPage", page);

        } else if ("ongs".equals(view)) {
            // [MODIFICARE] 20 de ONG-uri pe pagină
            int pageSize = 20;
            AdminService.PageWrapper<Ong> ongsPage = adminService.getOngsPage(page, pageSize, search);
            model.addAttribute("ongsPage", ongsPage);
            model.addAttribute("currentPage", page);
            model.addAttribute("currentSearch", search);

        } else if ("statistics".equals(view)) {
            model.addAttribute("stats", adminService.getSystemStatistics());
        }

        return "admin-dashboard";
    }

    @PostMapping("/users/create")
    public String createUser(@ModelAttribute User user,
                             @RequestParam(required = false) String coordinatorOngRegNumber,
                             @RequestParam(required = false) String department,
                             @RequestParam(required = false) Integer experienceYears,
                             @RequestParam(required = false) String employmentType,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
                             @RequestParam(required = false) String skills,
                             @RequestParam(required = false) String availability,
                             @RequestParam(required = false) String emergencyContact,
                             RedirectAttributes redirectAttributes) {
        try {
            adminService.createUser(user,
                    coordinatorOngRegNumber, department, experienceYears, employmentType,
                    birthDate, skills, availability, emergencyContact);

            redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/admin/dashboard?view=users";
    }

    @GetMapping("/users/edit/{id}")
    @ResponseBody
    public Map<String, Object> getUserForEdit(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        User user = adminService.getUserById(id);
        response.put("user", user);
        if (user.getRole() == User.Role.coordinator) {
            Coordinator coord = adminService.getCoordinatorDetailsByUserId(id);
            response.put("coordinator", coord);
        } else if (user.getRole() == User.Role.volunteer) {
            Volunteer vol = adminService.getVolunteerDetailsByUserId(id);
            response.put("volunteer", vol);
        }
        return response;
    }

    @GetMapping("/users/check-volunteer-status/{id}")
    @ResponseBody
    public boolean checkVolunteerStatus(@PathVariable Integer id) {
        return adminService.hasVolunteerConflicts(id);
    }

    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable Integer id,
                             @ModelAttribute User user,
                             @RequestParam(required = false) String coordinatorOngRegNumber,
                             @RequestParam(required = false) String department,
                             @RequestParam(required = false) Integer experienceYears,
                             @RequestParam(required = false) String employmentType,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
                             @RequestParam(required = false) String skills,
                             @RequestParam(required = false) String availability,
                             @RequestParam(required = false) String emergencyContact,
                             RedirectAttributes redirectAttributes) {
        try {
            adminService.updateUser(id, user,
                    coordinatorOngRegNumber, department, experienceYears, employmentType,
                    birthDate, skills, availability, emergencyContact);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/admin/dashboard?view=users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Integer id, RedirectAttributes redirectAttributes, Authentication authentication) {
        try {
            if (adminService.deleteUser(id, authentication.getName())) {
                redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "User not found!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/dashboard?view=users";
    }

    @PostMapping("/ongs/create")
    public String createOng(@ModelAttribute Ong ong, RedirectAttributes redirectAttributes) {
        try {
            adminService.createOng(ong);
            redirectAttributes.addFlashAttribute("successMessage", "ONG created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/admin/dashboard?view=ongs";
    }

    @GetMapping("/ongs/edit/{id}")
    @ResponseBody
    public Ong getOngForEdit(@PathVariable String id) {
        return adminService.getOngById(id);
    }

    @PostMapping("/ongs/update/{id}")
    public String updateOng(@PathVariable String id, @ModelAttribute Ong ong, RedirectAttributes redirectAttributes) {
        try {
            adminService.updateOng(id, ong);
            redirectAttributes.addFlashAttribute("successMessage", "ONG updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/admin/dashboard?view=ongs";
    }

    @PostMapping("/ongs/delete/{id}")
    public String deleteOng(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            if (adminService.deleteOng(id)) {
                redirectAttributes.addFlashAttribute("successMessage", "ONG deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "ONG not found!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/admin/dashboard?view=ongs";
    }
}