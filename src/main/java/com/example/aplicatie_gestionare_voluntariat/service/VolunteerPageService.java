package com.example.aplicatie_gestionare_voluntariat.service;

import com.example.aplicatie_gestionare_voluntariat.model.Activity;
import com.example.aplicatie_gestionare_voluntariat.model.Ong;
import com.example.aplicatie_gestionare_voluntariat.model.User;
import com.example.aplicatie_gestionare_voluntariat.model.Volunteer;
import com.example.aplicatie_gestionare_voluntariat.repository.ActivityRepository;
import com.example.aplicatie_gestionare_voluntariat.repository.OngRepository;
import com.example.aplicatie_gestionare_voluntariat.repository.UserRepository;
import com.example.aplicatie_gestionare_voluntariat.repository.VolunteerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VolunteerPageService {

    @Autowired
    private OngRepository ongRepository;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VolunteerRepository volunteerRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Ong> getAllOngs() {
        return ongRepository.findAll();
    }

    public Ong getOngById(String registrationNumber) {
        return ongRepository.findById(registrationNumber).orElse(null);
    }

    public Map<String, Object> getOngStatistics(String ongRegNumber) {
        Map<String, Object> stats = new HashMap<>();

        String sqlCoordinators = "SELECT u.first_name, u.last_name FROM coordinators c " +
                "JOIN users u ON c.id_user = u.id_user WHERE c.ong_registration_number = ?";
        List<String> coordinators = jdbcTemplate.query(sqlCoordinators, (rs, rowNum) ->
                rs.getString("first_name") + " " + rs.getString("last_name"), ongRegNumber);
        stats.put("coordinators", coordinators);

        // Actualizat JOIN: va.id_activity -> a.id_coordinator -> c.ong_registration_number
        String sqlUniqueVolunteers = "SELECT COUNT(DISTINCT va.id_volunteer) FROM volunteer_activities va " +
                "JOIN activities a ON va.id_activity = a.id_activity " +
                "JOIN coordinators c ON a.id_coordinator = c.id_coordinator " +
                "WHERE c.ong_registration_number = ? " +
                "AND va.status = 'accepted' " +
                "AND a.status != 'completed'";

        Long uniqueVolunteersCount = jdbcTemplate.queryForObject(sqlUniqueVolunteers, Long.class, ongRegNumber);
        if (uniqueVolunteersCount == null) uniqueVolunteersCount = 0L;

        long totalParticipants = uniqueVolunteersCount + coordinators.size();
        stats.put("totalVolunteers", totalParticipants);

        String sqlDonations = "SELECT COALESCE(SUM(amount), 0) FROM donations WHERE ong_registration_number = ?";
        Double totalDonations = jdbcTemplate.queryForObject(sqlDonations, Double.class, ongRegNumber);
        stats.put("totalDonations", totalDonations);

        return stats;
    }

    public List<Activity> getOngActivities(String ongRegNumber) {
        return activityRepository.findByOngId(ongRegNumber);
    }

    public Integer getVolunteerIdByEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            Volunteer vol = volunteerRepository.findByUserId(user.getIdUser()).orElse(null);
            return vol != null ? vol.getIdVolunteer() : null;
        }
        return null;
    }

    public void enrollInActivity(String userEmail, Integer activityId, String motivation) {
        Integer volunteerId = getVolunteerIdByEmail(userEmail);
        if (volunteerId != null) {
            if (!activityRepository.isEnrolled(volunteerId, activityId)) {
                activityRepository.enrollVolunteer(volunteerId, activityId, motivation);
            }
        }
    }
}