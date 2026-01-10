package com.example.aplicatie_gestionare_voluntariat.service;

import com.example.aplicatie_gestionare_voluntariat.model.Activity;
import com.example.aplicatie_gestionare_voluntariat.model.Coordinator;
import com.example.aplicatie_gestionare_voluntariat.model.Ong;
import com.example.aplicatie_gestionare_voluntariat.model.User;
import com.example.aplicatie_gestionare_voluntariat.repository.ActivityRepository;
import com.example.aplicatie_gestionare_voluntariat.repository.CoordinatorRepository;
import com.example.aplicatie_gestionare_voluntariat.repository.OngRepository;
import com.example.aplicatie_gestionare_voluntariat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class CoordinatorService {

    @Autowired
    private CoordinatorRepository coordinatorRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private OngRepository ongRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Regex
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    private boolean isValidEmail(String email) {
        return email != null && Pattern.matches(EMAIL_PATTERN, email);
    }

    public Coordinator getCoordinatorByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Coordinator coordinator = coordinatorRepository.findByUserId(user.getIdUser()).orElseThrow(() -> new RuntimeException("Coordinator profile not found"));
        coordinator.setUser(user);
        return coordinator;
    }

    public Ong getOngForCoordinator(Coordinator coordinator) { return ongRepository.findById(coordinator.getOngRegistrationNumber()).orElse(new Ong()); }

    public Map<String, Object> getDashboardStats(Coordinator coordinator) {
        Map<String, Object> stats = new HashMap<>();
        String sqlActivities = "SELECT COUNT(*) FROM activities WHERE id_coordinator = ?";
        Integer totalActivities = jdbcTemplate.queryForObject(sqlActivities, Integer.class, coordinator.getIdCoordinator());
        stats.put("totalActivities", totalActivities);
        String sqlPending = "SELECT COUNT(*) FROM volunteer_activities va JOIN activities a ON va.id_activity = a.id_activity WHERE a.id_coordinator = ? AND va.status = 'pending'";
        Integer pendingRequests = jdbcTemplate.queryForObject(sqlPending, Integer.class, coordinator.getIdCoordinator());
        stats.put("pendingRequests", pendingRequests);
        String sqlActiveVol = "SELECT COUNT(DISTINCT va.id_volunteer) FROM volunteer_activities va JOIN activities a ON va.id_activity = a.id_activity WHERE a.id_coordinator = ? AND va.status = 'accepted'";
        Integer activeVolunteers = jdbcTemplate.queryForObject(sqlActiveVol, Integer.class, coordinator.getIdCoordinator());
        stats.put("activeVolunteers", activeVolunteers);
        String sqlDonations = "SELECT COALESCE(SUM(amount), 0) FROM donations WHERE ong_registration_number = ?";
        Double totalDonations = jdbcTemplate.queryForObject(sqlDonations, Double.class, coordinator.getOngRegistrationNumber());
        stats.put("totalDonations", totalDonations);
        return stats;
    }

    public List<Activity> getActivitiesByCoordinator(Integer coordinatorId) { return activityRepository.findByCoordinatorId(coordinatorId); }
    public Activity getActivityById(Integer activityId) { return activityRepository.findById(activityId).orElse(null); }

    @Transactional
    public void createActivity(Activity activity, Coordinator coordinator) {
        // Validări de bază
        if (activity.getName() == null || activity.getName().trim().isEmpty()) throw new IllegalArgumentException("Activity Name is required");
        if (activity.getDescription() == null || activity.getDescription().trim().isEmpty()) throw new IllegalArgumentException("Description is required");
        if (activity.getLocation() == null || activity.getLocation().trim().isEmpty()) throw new IllegalArgumentException("Location is required");
        if (activity.getStartDate() == null) throw new IllegalArgumentException("Start Date is required");
        if (activity.getEndDate() == null) throw new IllegalArgumentException("End Date is required");
        if (activity.getMaxVolunteers() == null || activity.getMaxVolunteers() < 1) throw new IllegalArgumentException("Max Volunteers must be at least 1");

        // Validare logică timp
        if (activity.getEndDate().isBefore(activity.getStartDate())) {
            throw new IllegalArgumentException("End Date cannot be before Start Date");
        }
        if (activity.getStartDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot schedule activities in the past");
        }

        // [NOU] Validare suprapunere activități pentru același coordonator
        if (activityRepository.hasOverlappingActivity(coordinator.getIdCoordinator(), activity.getStartDate(), activity.getEndDate())) {
            throw new IllegalArgumentException("You already have an activity scheduled during this time interval. Please choose different hours.");
        }

        activity.setIdCoordinator(coordinator.getIdCoordinator());
        if (activity.getIdCategory() == null) activity.setIdCategory(1);

        // Activitatea se salvează implicit cu status 'open' (din repository)
        activityRepository.save(activity);
    }

    public void closeEnrollment(Integer activityId) { activityRepository.updateStatus(activityId, "closed"); }

    public List<Map<String, Object>> getApplicantsForActivity(Integer activityId, String statusFilter) {
        StringBuilder sql = new StringBuilder("SELECT va.id_volunteer, va.enrollment_date, va.status, va.feedback, u.first_name, u.last_name, u.email, u.phone_number, v.skills FROM volunteer_activities va JOIN volunteers v ON va.id_volunteer = v.id_volunteer JOIN users u ON v.id_user = u.id_user WHERE va.id_activity = ?");
        List<Object> params = new ArrayList<>();
        params.add(activityId);
        if (statusFilter != null && !statusFilter.isEmpty() && !statusFilter.equals("all")) { sql.append(" AND va.status = ?"); params.add(statusFilter); }
        sql.append(" ORDER BY va.enrollment_date DESC");
        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    public Map<String, Long> getActivityStatistics(Integer activityId) {
        Map<String, Long> stats = new HashMap<>();
        String sqlAccepted = "SELECT COUNT(*) FROM volunteer_activities WHERE id_activity = ? AND status = 'accepted'";
        Long accepted = jdbcTemplate.queryForObject(sqlAccepted, Long.class, activityId);
        stats.put("accepted", accepted != null ? accepted : 0L);
        String sqlPending = "SELECT COUNT(*) FROM volunteer_activities WHERE id_activity = ? AND status = 'pending'";
        Long pending = jdbcTemplate.queryForObject(sqlPending, Long.class, activityId);
        stats.put("pending", pending != null ? pending : 0L);
        String sqlTotal = "SELECT COUNT(*) FROM volunteer_activities WHERE id_activity = ?";
        Long total = jdbcTemplate.queryForObject(sqlTotal, Long.class, activityId);
        stats.put("total", total != null ? total : 0L);
        return stats;
    }

    public List<Map<String, Object>> getVolunteersForCoordinator(Integer coordinatorId) {
        String sql = "SELECT v.id_volunteer, u.first_name, u.last_name, u.email, u.phone_number, v.skills, STRING_AGG(DISTINCT a.name, ', ') as activity_list FROM volunteer_activities va JOIN activities a ON va.id_activity = a.id_activity JOIN volunteers v ON va.id_volunteer = v.id_volunteer JOIN users u ON v.id_user = u.id_user WHERE a.id_coordinator = ? AND va.status = 'accepted' GROUP BY v.id_volunteer, u.first_name, u.last_name, u.email, u.phone_number, v.skills";
        return jdbcTemplate.queryForList(sql, coordinatorId);
    }

    @Transactional
    public void updateApplicationStatus(Integer activityId, Integer volunteerId, String newStatus) {
        String checkSql = "SELECT status FROM volunteer_activities WHERE id_activity = ? AND id_volunteer = ?";
        try {
            String currentStatus = jdbcTemplate.queryForObject(checkSql, String.class, activityId, volunteerId);
            if (!"pending".equalsIgnoreCase(currentStatus)) return;
        } catch (Exception e) { return; }

        String sql = "UPDATE volunteer_activities SET status = ? WHERE id_activity = ? AND id_volunteer = ?";
        jdbcTemplate.update(sql, newStatus, activityId, volunteerId);

        // Dacă a fost acceptat, verificăm dacă s-a atins capacitatea maximă pentru a închide automat înscrierile
        if ("accepted".equalsIgnoreCase(newStatus)) {
            String countSql = "SELECT COUNT(*) FROM volunteer_activities WHERE id_activity = ? AND status = 'accepted'";
            Integer acceptedCount = jdbcTemplate.queryForObject(countSql, Integer.class, activityId);
            Activity activity = activityRepository.findById(activityId).orElse(null);

            // Inchidem (closed) doar dacă activitatea nu e deja completed sau in_progress
            if (activity != null &&
                    acceptedCount != null &&
                    acceptedCount >= activity.getMaxVolunteers() &&
                    "open".equals(activity.getStatus())) {

                activityRepository.updateStatus(activityId, "closed");
            }
        }
    }

    public List<Map<String, Object>> getAllCategories() { return jdbcTemplate.queryForList("SELECT id_category, name FROM activity_categories"); }

    @Transactional
    public void updateCoordinatorProfile(Coordinator updatedCoordinator, String currentUserEmail) {
        if (updatedCoordinator.getDepartment() == null || updatedCoordinator.getDepartment().trim().isEmpty()) throw new IllegalArgumentException("Department cannot be empty");
        if (updatedCoordinator.getExperienceYears() == null) throw new IllegalArgumentException("Experience Years cannot be empty");
        if (updatedCoordinator.getEmploymentType() == null || updatedCoordinator.getEmploymentType().trim().isEmpty()) throw new IllegalArgumentException("Employment Type cannot be empty");

        User user = userRepository.findByEmail(currentUserEmail).orElseThrow(() -> new RuntimeException("User not found"));
        Coordinator existingCoordinator = coordinatorRepository.findByUserId(user.getIdUser()).orElseThrow(() -> new RuntimeException("Coordinator not found"));

        if (updatedCoordinator.getUser() != null) {
            if (updatedCoordinator.getUser().getFirstName() == null || updatedCoordinator.getUser().getFirstName().trim().isEmpty()) throw new IllegalArgumentException("First Name cannot be empty");
            if (updatedCoordinator.getUser().getLastName() == null || updatedCoordinator.getUser().getLastName().trim().isEmpty()) throw new IllegalArgumentException("Last Name cannot be empty");

            user.setFirstName(updatedCoordinator.getUser().getFirstName());
            user.setLastName(updatedCoordinator.getUser().getLastName());
            user.setPhoneNumber(updatedCoordinator.getUser().getPhoneNumber());
            userRepository.save(user);
        }

        existingCoordinator.setDepartment(updatedCoordinator.getDepartment());
        existingCoordinator.setExperienceYears(updatedCoordinator.getExperienceYears());
        existingCoordinator.setEmploymentType(updatedCoordinator.getEmploymentType());

        coordinatorRepository.save(existingCoordinator);
    }

    @Transactional
    public void deleteCoordinatorAccount(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Coordinator coordinator = coordinatorRepository.findByUserId(user.getIdUser()).orElse(null);
        if (coordinator != null) {
            String sqlDeleteEnrollments = "DELETE FROM volunteer_activities WHERE id_activity IN (SELECT id_activity FROM activities WHERE id_coordinator = ?)";
            jdbcTemplate.update(sqlDeleteEnrollments, coordinator.getIdCoordinator());
            String sqlDeleteActivities = "DELETE FROM activities WHERE id_coordinator = ?";
            jdbcTemplate.update(sqlDeleteActivities, coordinator.getIdCoordinator());
            coordinatorRepository.deleteByUserId(user.getIdUser());
        }
        userRepository.deleteById(user.getIdUser());
    }

    // [NOU] SCHEDULER: Rulează la fiecare minut pentru a actualiza statusurile
    @Scheduled(fixedRate = 60000) // 60000 ms = 1 minut
    @Transactional
    public void updateActivityStatusesAutomatically() {
        LocalDateTime now = LocalDateTime.now();

        // 1. Trecem la 'in_progress' activitățile care au început și nu sunt încă marcate ca atare sau terminate
        // Aceasta suprascrie 'open' sau 'closed' (dacă erau închise pentru înscrieri, acum sunt în progres fizic)
        activityRepository.updateStatusToInProgress(now);

        // 2. Trecem la 'completed' activitățile care s-au terminat
        activityRepository.updateStatusToCompleted(now);

        // Notă: Folosind @Transactional, aceste update-uri sunt atomice per rulare.
    }
}