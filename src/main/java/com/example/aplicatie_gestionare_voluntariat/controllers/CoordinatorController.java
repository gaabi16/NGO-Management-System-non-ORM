package com.example.aplicatie_gestionare_voluntariat.controllers;

import com.example.aplicatie_gestionare_voluntariat.model.Activity;
import com.example.aplicatie_gestionare_voluntariat.model.Coordinator;
import com.example.aplicatie_gestionare_voluntariat.model.Ong;
import com.example.aplicatie_gestionare_voluntariat.service.CoordinatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        model.addAttribute("currentView", view); // Poate fi null

        // Încărcare condițională. Dacă view e null, nu încărcăm nimic.
        if ("volunteers".equals(view)) {
            List<Map<String, Object>> volunteers = coordinatorService.getVolunteersForCoordinator(coordinator.getIdCoordinator());
            model.addAttribute("volunteers", volunteers);
        } else if ("activities".equals(view)) {
            List<Activity> activities = coordinatorService.getActivitiesByCoordinator(coordinator.getIdCoordinator());
            List<Map<String, Object>> categories = coordinatorService.getAllCategories();
            model.addAttribute("activities", activities);
            model.addAttribute("categories", categories);
            model.addAttribute("newActivity", new Activity());
        }

        return "coordinator-dashboard";
    }

    @PostMapping("/activities/create")
    public String createActivity(@ModelAttribute Activity activity, Authentication authentication) {
        Coordinator coordinator = coordinatorService.getCoordinatorByEmail(authentication.getName());
        coordinatorService.createActivity(activity, coordinator);
        return "redirect:/coordinator/dashboard?view=activities&success=ActivityCreated";
    }

    @PostMapping("/activities/close/{id}")
    public String closeEnrollment(@PathVariable Integer id) {
        coordinatorService.closeEnrollment(id);
        return "redirect:/coordinator/dashboard?view=activities&success=EnrollmentClosed";
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

        return "coordinator-activity-details";
    }

    @PostMapping("/applications/update")
    public String updateApplicationStatus(@RequestParam Integer activityId,
                                          @RequestParam Integer volunteerId,
                                          @RequestParam String status) {
        coordinatorService.updateApplicationStatus(activityId, volunteerId, status);
        return "redirect:/coordinator/activities/" + activityId + "?success=StatusUpdated";
    }
}