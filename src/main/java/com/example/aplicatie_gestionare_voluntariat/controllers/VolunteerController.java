package com.example.aplicatie_gestionare_voluntariat.controllers;

import com.example.aplicatie_gestionare_voluntariat.dto.VolunteerRegistrationDto;
import com.example.aplicatie_gestionare_voluntariat.model.Activity;
import com.example.aplicatie_gestionare_voluntariat.model.Ong;
import com.example.aplicatie_gestionare_voluntariat.model.User;
import com.example.aplicatie_gestionare_voluntariat.repository.UserRepository;
import com.example.aplicatie_gestionare_voluntariat.service.UserService;
import com.example.aplicatie_gestionare_voluntariat.service.VolunteerPageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/volunteer")
public class VolunteerController {

    @Autowired
    private VolunteerPageService volunteerPageService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(new User());
        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("ongs", volunteerPageService.getAllOngs());
        return "volunteer-dashboard";
    }

    @GetMapping("/ong/{id}")
    public String ongDetails(@PathVariable String id, Model model, Authentication authentication) {
        Ong ong = volunteerPageService.getOngById(id);
        if (ong == null) return "redirect:/volunteer/dashboard";

        Map<String, Object> stats = volunteerPageService.getOngStatistics(id);
        List<Activity> activities = volunteerPageService.getOngActivities(id);
        User user = userRepository.findByEmail(authentication.getName()).orElse(new User());

        model.addAttribute("ong", ong);
        model.addAttribute("stats", stats);
        model.addAttribute("activities", activities);
        model.addAttribute("firstName", user.getFirstName());
        return "ong-details";
    }

    @PostMapping("/activity/{id}/join")
    public String joinActivity(@PathVariable Integer id,
                               @RequestParam("ongId") String ongId,
                               @RequestParam(value = "motivation", required = false) String motivation,
                               Authentication authentication) {
        volunteerPageService.enrollInActivity(authentication.getName(), id, motivation);
        return "redirect:/volunteer/ong/" + ongId + "?success=true";
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        VolunteerRegistrationDto profileData = userService.getVolunteerProfile(authentication.getName());
        model.addAttribute("profile", profileData);
        return "volunteer-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute VolunteerRegistrationDto profileDto, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            userService.updateVolunteerProfile(authentication.getName(), profileDto);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/volunteer/profile";
    }

    @PostMapping("/profile/delete")
    public String deleteAccount(Authentication authentication, HttpServletRequest request) {
        userService.deleteVolunteerAccount(authentication.getName());
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        return "redirect:/login?accountDeleted=true";
    }
}