package com.example.aplicatie_gestionare_voluntariat.controllers;

import com.example.aplicatie_gestionare_voluntariat.model.Activity;
import com.example.aplicatie_gestionare_voluntariat.model.Coordinator;
import com.example.aplicatie_gestionare_voluntariat.model.Donation;
import com.example.aplicatie_gestionare_voluntariat.model.Ong;
import com.example.aplicatie_gestionare_voluntariat.service.CoordinatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/coordinator")
public class CoordinatorController {

    @Autowired
    private CoordinatorService coordinatorService;

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String view,
                            Model model,
                            Authentication authentication) {

        Coordinator coordinator = coordinatorService.getCoordinatorByEmail(authentication.getName());
        Ong ong = coordinatorService.getOngForCoordinator(coordinator);
        Map<String, Object> stats = coordinatorService.getDashboardStats(coordinator);

        model.addAttribute("ong", ong);
        model.addAttribute("stats", stats);
        model.addAttribute("currentView", view);

        if ("volunteers".equals(view)) {
            List<Map<String, Object>> volunteers = coordinatorService.getVolunteersForCoordinator(coordinator.getIdCoordinator());
            model.addAttribute("volunteers", volunteers);
        } else if ("activities".equals(view)) {
            List<Activity> activities = coordinatorService.getActivitiesByCoordinator(coordinator.getIdCoordinator());
            List<Map<String, Object>> categories = coordinatorService.getAllCategories();
            model.addAttribute("activities", activities);
            model.addAttribute("categories", categories);

            if (!model.containsAttribute("newActivity")) {
                model.addAttribute("newActivity", new Activity());
            }
        }

        return "coordinator-dashboard";
    }

    @PostMapping("/activities/create")
    public String createActivity(@ModelAttribute Activity activity, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Coordinator coordinator = coordinatorService.getCoordinatorByEmail(authentication.getName());
            coordinatorService.createActivity(activity, coordinator);
            redirectAttributes.addFlashAttribute("successMessage", "Activity created successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("createActivityError", e.getMessage());
            redirectAttributes.addFlashAttribute("newActivity", activity);
            redirectAttributes.addAttribute("view", "activities");
            return "redirect:/coordinator/dashboard";
        }
        return "redirect:/coordinator/dashboard?view=activities";
    }

    @PostMapping("/activities/close/{id}")
    public String closeEnrollment(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        coordinatorService.closeEnrollment(id);
        redirectAttributes.addAttribute("success", "EnrollmentClosed");
        return "redirect:/coordinator/dashboard?view=activities";
    }

    @GetMapping("/activities/{id}")
    public String viewActivityDetails(@PathVariable Integer id,
                                      @RequestParam(required = false, defaultValue = "all") String status,
                                      Model model,
                                      Authentication authentication) {
        Activity activity = coordinatorService.getActivityById(id);
        if (activity == null) return "redirect:/coordinator/dashboard";

        List<Map<String, Object>> applicants = coordinatorService.getApplicantsForActivity(id, status);
        Map<String, Long> activityStats = coordinatorService.getActivityStatistics(id);

        model.addAttribute("activity", activity);
        model.addAttribute("applicants", applicants);
        model.addAttribute("currentFilter", status);

        model.addAttribute("enrolledCount", activityStats.get("accepted"));
        model.addAttribute("pendingCount", activityStats.get("pending"));
        model.addAttribute("totalApplicants", activityStats.get("total"));

        Double actualDonation = coordinatorService.getActualDonationAmount(id);
        model.addAttribute("actualDonationAmount", actualDonation);

        model.addAttribute("isDonationRegistered", actualDonation > 0);

        if (!model.containsAttribute("newDonation")) {
            Donation donation = new Donation();
            donation.setDonorName(activity.getName());
            model.addAttribute("newDonation", donation);
        }

        return "coordinator-activity-details";
    }

    @PostMapping("/activities/{id}/register-donation")
    public String registerDonation(@PathVariable Integer id,
                                   @ModelAttribute Donation donation,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        try {
            Coordinator coordinator = coordinatorService.getCoordinatorByEmail(authentication.getName());
            coordinatorService.addDonation(donation, coordinator);

            redirectAttributes.addFlashAttribute("successMessage", "Donation registered successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error registering donation: " + e.getMessage());
        }

        return "redirect:/coordinator/activities/" + id;
    }

    @PostMapping("/applications/update")
    public String updateApplicationStatus(@RequestParam Integer activityId,
                                          @RequestParam Integer volunteerId,
                                          @RequestParam String status) {
        coordinatorService.updateApplicationStatus(activityId, volunteerId, status);
        return "redirect:/coordinator/activities/" + activityId + "?success=StatusUpdated";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Authentication authentication) {
        Coordinator coordinator = coordinatorService.getCoordinatorByEmail(authentication.getName());
        model.addAttribute("profile", coordinator);
        return "coordinator-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute Coordinator coordinator, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            String currentUserEmail = authentication.getName();
            coordinatorService.updateCoordinatorProfile(coordinator, currentUserEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/coordinator/profile";
    }

    @PostMapping("/profile/delete")
    public String deleteAccount(Authentication authentication) {
        String currentUserEmail = authentication.getName();
        coordinatorService.deleteCoordinatorAccount(currentUserEmail);
        return "redirect:/logout";
    }
}