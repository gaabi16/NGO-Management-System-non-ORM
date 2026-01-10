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
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    // Mapare statică pentru demo (Continent -> Lista de țări)
    private static final Map<String, List<String>> CONTINENT_MAP = new HashMap<>();
    static {
        CONTINENT_MAP.put("Europe", Arrays.asList("Romania", "France", "Germany", "Italy", "Spain", "UK", "Poland", "Ukraine", "Netherlands", "Belgium"));
        CONTINENT_MAP.put("North America", Arrays.asList("USA", "Canada", "Mexico"));
        CONTINENT_MAP.put("Asia", Arrays.asList("China", "Japan", "India", "South Korea", "Vietnam"));
        CONTINENT_MAP.put("Africa", Arrays.asList("Egypt", "South Africa", "Nigeria", "Kenya"));
        CONTINENT_MAP.put("South America", Arrays.asList("Brazil", "Argentina", "Chile", "Colombia"));
        CONTINENT_MAP.put("Australia", Arrays.asList("Australia", "New Zealand"));
    }

    public Map<String, List<String>> getLocationData() {
        return CONTINENT_MAP;
    }

    public List<Ong> getOngsFiltered(int page, int size, String continent, String country) {
        StringBuilder sql = new StringBuilder("SELECT * FROM ongs WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (country != null && !country.isEmpty()) {
            sql.append("AND country = ? ");
            params.add(country);
        } else if (continent != null && !continent.isEmpty()) {
            List<String> countries = CONTINENT_MAP.getOrDefault(continent, new ArrayList<>());
            if (!countries.isEmpty()) {
                String placeholders = String.join(",", Collections.nCopies(countries.size(), "?"));
                sql.append("AND country IN (").append(placeholders).append(") ");
                params.addAll(countries);
            } else {
                sql.append("AND 1=0 ");
            }
        }

        sql.append("ORDER BY name ASC LIMIT ? OFFSET ?");
        params.add(size);
        params.add(page * size);

        return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<>(Ong.class), params.toArray());
    }

    public long countOngsFiltered(String continent, String country) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM ongs WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (country != null && !country.isEmpty()) {
            sql.append("AND country = ? ");
            params.add(country);
        } else if (continent != null && !continent.isEmpty()) {
            List<String> countries = CONTINENT_MAP.getOrDefault(continent, new ArrayList<>());
            if (!countries.isEmpty()) {
                String placeholders = String.join(",", Collections.nCopies(countries.size(), "?"));
                sql.append("AND country IN (").append(placeholders).append(") ");
                params.addAll(countries);
            } else {
                sql.append("AND 1=0 ");
            }
        }

        Long count = jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
        return count != null ? count : 0;
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

    // [MODIFICAT] Acum acceptă emailul userului pentru a verifica dacă a aplicat deja
    public List<Activity> getOngActivities(String ongRegNumber, String userEmail) {
        List<Activity> activities = activityRepository.findByOngId(ongRegNumber);

        Integer volunteerId = getVolunteerIdByEmail(userEmail);

        if (volunteerId != null) {
            // Obținem ID-urile activităților unde voluntarul este înscris (orice status)
            String sqlCheckEnrollment = "SELECT id_activity FROM volunteer_activities WHERE id_volunteer = ?";
            List<Integer> enrolledActivityIds = jdbcTemplate.queryForList(sqlCheckEnrollment, Integer.class, volunteerId);

            Set<Integer> enrolledSet = new HashSet<>(enrolledActivityIds);

            for (Activity activity : activities) {
                if (enrolledSet.contains(activity.getIdActivity())) {
                    activity.setEnrolled(true);
                }
            }
        }

        return activities;
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