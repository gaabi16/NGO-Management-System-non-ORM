package com.example.aplicatie_gestionare_voluntariat.controllers;

import com.example.aplicatie_gestionare_voluntariat.model.Activity;
import com.example.aplicatie_gestionare_voluntariat.model.Ong;
import com.example.aplicatie_gestionare_voluntariat.model.User;
import com.example.aplicatie_gestionare_voluntariat.repository.UserRepository;
import com.example.aplicatie_gestionare_voluntariat.service.VolunteerPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/volunteer")
public class VolunteerController {

    @Autowired
    private VolunteerPageService volunteerPageService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(new User());

        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("ongs", volunteerPageService.getAllOngs());

        return "volunteer-dashboard";
    }

    @GetMapping("/ong/{id}")
    public String ongDetails(@PathVariable Integer id, Model model, Authentication authentication) {
        Ong ong = volunteerPageService.getOngById(id);
        if (ong == null) {
            return "redirect:/volunteer/dashboard";
        }

        Map<String, Object> stats = volunteerPageService.getOngStatistics(id);
        List<Activity> activities = volunteerPageService.getOngActivities(id); // Acum este List<Activity>
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(new User());

        model.addAttribute("ong", ong);
        model.addAttribute("stats", stats);
        model.addAttribute("activities", activities);
        model.addAttribute("firstName", user.getFirstName());

        return "ong-details";
    }

    @PostMapping("/activity/{id}/join")
    public String joinActivity(@PathVariable Integer id,
                               @RequestParam("ongId") Integer ongId,
                               @RequestParam(value = "motivation", required = false) String motivation,
                               Authentication authentication) {
        volunteerPageService.enrollInActivity(authentication.getName(), id, motivation);
        return "redirect:/volunteer/ong/" + ongId + "?success=true";
    }
}