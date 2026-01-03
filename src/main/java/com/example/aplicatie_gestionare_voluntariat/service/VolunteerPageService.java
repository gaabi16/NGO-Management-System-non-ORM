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

    public Ong getOngById(Integer id) {
        return ongRepository.findById(id).orElse(null);
    }

    public Map<String, Object> getOngStatistics(Integer ongId) {
        Map<String, Object> stats = new HashMap<>();

        // 1. Obținem lista de coordonatori (pentru a-i afișa și pentru a le afla numărul)
        String sqlCoordinators = "SELECT u.first_name, u.last_name FROM coordinators c " +
                "JOIN users u ON c.id_user = u.id_user WHERE c.id_ong = ?";
        List<String> coordinators = jdbcTemplate.query(sqlCoordinators, (rs, rowNum) ->
                rs.getString("first_name") + " " + rs.getString("last_name"), ongId);
        stats.put("coordinators", coordinators);

        // 2. Calculăm numărul total de participanți
        // Formula: (Voluntari unici la activități) + (Total Coordonatori)

        // Folosim DISTINCT pentru a număra un voluntar o singură dată, chiar dacă e la 5 activități
        String sqlUniqueVolunteers = "SELECT COUNT(DISTINCT va.id_volunteer) FROM volunteer_activities va " +
                "JOIN activities a ON va.id_activity = a.id_activity WHERE a.id_ong = ?";

        Long uniqueVolunteersCount = jdbcTemplate.queryForObject(sqlUniqueVolunteers, Long.class, ongId);
        if (uniqueVolunteersCount == null) uniqueVolunteersCount = 0L;

        // Adunăm voluntarii unici cu numărul de coordonatori
        long totalParticipants = uniqueVolunteersCount + coordinators.size();

        stats.put("totalVolunteers", totalParticipants);

        // 3. Total Donații
        String sqlDonations = "SELECT COALESCE(SUM(amount), 0) FROM donations WHERE id_ong = ?";
        Double totalDonations = jdbcTemplate.queryForObject(sqlDonations, Double.class, ongId);
        stats.put("totalDonations", totalDonations);

        return stats;
    }

    public List<Activity> getOngActivities(Integer ongId) {
        return activityRepository.findByOngId(ongId);
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