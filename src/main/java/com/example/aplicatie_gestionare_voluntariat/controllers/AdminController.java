package com.example.aplicatie_gestionare_voluntariat.controllers;

import com.example.aplicatie_gestionare_voluntariat.model.User;
import com.example.aplicatie_gestionare_voluntariat.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
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
            Model model,
            Authentication authentication) {

        // Verifică dacă utilizatorul este admin
        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_admin"))) {
            return "redirect:/home";
        }

        model.addAttribute("currentView", view);
        model.addAttribute("currentUserEmail", authentication.getName());

        // Încarcă datele în funcție de parametrul view
        if ("users".equals(view)) {
            AdminService.PageWrapper<User> usersPage;

            if (roles != null && !roles.isEmpty()) {
                // Convert String roles to User.Role enum
                List<User.Role> roleEnums = roles.stream()
                        .map(User.Role::valueOf)
                        .collect(Collectors.toList());
                usersPage = adminService.getUsersPageByRoles(page, 50, roleEnums);
            } else {
                usersPage = adminService.getUsersPage(page, 50);
            }

            model.addAttribute("usersPage", usersPage);
            model.addAttribute("currentPage", page);

            // Debug
            System.out.println("=== DEBUG INFO ===");
            System.out.println("View: " + view);
            System.out.println("Current page: " + page);
            System.out.println("Filtered roles: " + roles);
            System.out.println("Total users: " + usersPage.getTotalElements());
            System.out.println("Total pages: " + usersPage.getTotalPages());
            System.out.println("Users on this page: " + usersPage.getContent().size());
            System.out.println("Current user email: " + authentication.getName());
            System.out.println("==================");

        } else if ("ongs".equals(view)) {
            model.addAttribute("ongs", adminService.getFirst5Ongs());
        } else if ("coordinators".equals(view)) {
            model.addAttribute("coordinators", adminService.getFirst5Coordinators());
        }

        return "admin-dashboard";
    }

    @PostMapping("/users/create")
    public String createUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            adminService.createUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating user: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/dashboard?view=users";
    }

    @GetMapping("/users/edit/{id}")
    @ResponseBody
    public User getUserForEdit(@PathVariable Integer id) {
        return adminService.getUserById(id);
    }

    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable Integer id, @ModelAttribute User user,
                             RedirectAttributes redirectAttributes) {
        try {
            adminService.updateUser(id, user);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating user: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/dashboard?view=users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Integer id, RedirectAttributes redirectAttributes, Authentication authentication) {
        try {
            String currentUserEmail = authentication.getName();
            boolean deleted = adminService.deleteUser(id, currentUserEmail);
            if (deleted) {
                redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "User not found!");
            }
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/dashboard?view=users";
    }
}