package com.example.aplicatie_gestionare_voluntariat.repository;

import com.example.aplicatie_gestionare_voluntariat.model.Activity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ActivityRepository {

    private final JdbcTemplate jdbcTemplate;

    public ActivityRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Activity> activityRowMapper = new RowMapper<Activity>() {
        @Override
        public Activity mapRow(ResultSet rs, int rowNum) throws SQLException {
            Activity activity = new Activity();
            activity.setIdActivity(rs.getInt("id_activity"));
            activity.setIdCategory(rs.getInt("id_category"));
            activity.setIdCoordinator(rs.getInt("id_coordinator"));
            activity.setName(rs.getString("name"));
            activity.setDescription(rs.getString("description"));
            activity.setLocation(rs.getString("location"));

            Timestamp start = rs.getTimestamp("start_date");
            if (start != null) activity.setStartDate(start.toLocalDateTime());

            Timestamp end = rs.getTimestamp("end_date");
            if (end != null) activity.setEndDate(end.toLocalDateTime());

            activity.setMaxVolunteers(rs.getInt("max_volunteers"));
            activity.setStatus(rs.getString("status"));

            activity.setTargetDonation(rs.getDouble("target_donation"));

            try {
                activity.setCategoryName(rs.getString("category_name"));
            } catch (SQLException e) { /* ignore */ }

            try {
                activity.setPendingCount(rs.getInt("pending_count"));
            } catch (SQLException e) {
                activity.setPendingCount(0);
            }

            try {
                activity.setEnrollmentStatus(rs.getString("enrollment_status"));
            } catch (SQLException e) { }

            try {
                String first = rs.getString("coord_first");
                String last = rs.getString("coord_last");
                if (first != null && last != null) {
                    activity.setCoordinatorName(first + " " + last);
                }
                activity.setCoordinatorEmail(rs.getString("coord_email"));
                activity.setCoordinatorPhone(rs.getString("coord_phone"));
                activity.setOngName(rs.getString("ong_name"));
            } catch (SQLException e) { }

            try {
                String regNum = rs.getString("ong_registration_number");
                if (regNum != null) {
                    activity.setOngRegistrationNumber(regNum);
                }
            } catch (SQLException e) { }

            return activity;
        }
    };

    public List<Activity> findByOngId(String ongRegistrationNumber) {
        String sql = "SELECT a.*, cat.name as category_name, " +
                "u.first_name as coord_first, u.last_name as coord_last, " +
                "u.email as coord_email, u.phone_number as coord_phone " +
                "FROM activities a " +
                "JOIN coordinators c ON a.id_coordinator = c.id_coordinator " +
                "JOIN users u ON c.id_user = u.id_user " +
                "LEFT JOIN activity_categories cat ON a.id_category = cat.id_category " +
                "WHERE c.ong_registration_number = ? " +
                "ORDER BY a.start_date DESC";

        return jdbcTemplate.query(sql, activityRowMapper, ongRegistrationNumber);
    }

    public List<Activity> findByCoordinatorId(Integer coordinatorId) {
        String sql = "SELECT a.*, cat.name as category_name, " +
                "(SELECT COUNT(*) FROM volunteer_activities va WHERE va.id_activity = a.id_activity AND va.status = 'pending') as pending_count " +
                "FROM activities a " +
                "LEFT JOIN activity_categories cat ON a.id_category = cat.id_category " +
                "WHERE a.id_coordinator = ? " +
                "ORDER BY a.start_date DESC";
        return jdbcTemplate.query(sql, activityRowMapper, coordinatorId);
    }

    public List<Activity> findActivitiesByVolunteerId(Integer volunteerId, String statusFilter) {
        StringBuilder sql = new StringBuilder(
                "SELECT a.*, va.status as enrollment_status, cat.name as category_name, " +
                        "u.first_name as coord_first, u.last_name as coord_last, u.email as coord_email, u.phone_number as coord_phone, " +
                        "o.name as ong_name, o.registration_number as ong_registration_number " +
                        "FROM activities a " +
                        "JOIN volunteer_activities va ON a.id_activity = va.id_activity " +
                        "LEFT JOIN activity_categories cat ON a.id_category = cat.id_category " +
                        "JOIN coordinators c ON a.id_coordinator = c.id_coordinator " +
                        "JOIN users u ON c.id_user = u.id_user " +
                        "JOIN ongs o ON c.ong_registration_number = o.registration_number " +
                        "WHERE va.id_volunteer = ? ");

        List<Object> params = new ArrayList<>();
        params.add(volunteerId);

        if (statusFilter != null && !statusFilter.isEmpty() && !statusFilter.equals("all")) {
            sql.append("AND va.status = ? ");
            params.add(statusFilter);
        }

        sql.append("ORDER BY a.start_date DESC");

        return jdbcTemplate.query(sql.toString(), activityRowMapper, params.toArray());
    }

    public List<Activity> findRecommendationsForVolunteer(Integer volunteerId) {
        String sql = "SELECT DISTINCT a.*, cat.name as category_name, " +
                "u.first_name as coord_first, u.last_name as coord_last, " +
                "u.email as coord_email, u.phone_number as coord_phone, " +
                "o.name as ong_name, o.registration_number as ong_registration_number " +
                "FROM activities a " +
                "JOIN coordinators c ON a.id_coordinator = c.id_coordinator " +
                "JOIN users u ON c.id_user = u.id_user " +
                "JOIN ongs o ON c.ong_registration_number = o.registration_number " +
                "LEFT JOIN activity_categories cat ON a.id_category = cat.id_category " +
                "WHERE o.country IN (" +
                "    SELECT DISTINCT o2.country " +
                "    FROM volunteer_activities va2 " +
                "    JOIN activities a2 ON va2.id_activity = a2.id_activity " +
                "    JOIN coordinators c2 ON a2.id_coordinator = c2.id_coordinator " +
                "    JOIN ongs o2 ON c2.ong_registration_number = o2.registration_number " +
                "    WHERE va2.id_volunteer = ? AND va2.status IN ('accepted', 'completed')" +
                ") " +
                "AND a.id_activity NOT IN (" +
                "    SELECT va3.id_activity FROM volunteer_activities va3 WHERE va3.id_volunteer = ?" +
                ") " +
                "AND a.status = 'open' " +
                "ORDER BY a.start_date ASC LIMIT 3";

        return jdbcTemplate.query(sql, activityRowMapper, volunteerId, volunteerId);
    }

    public void autoCloseFinishedActivities() {
        String sqlVolunteers =
                "UPDATE volunteer_activities va " +
                        "SET status = 'completed', " +
                        "    hours_completed = GREATEST(1, ROUND((EXTRACT(EPOCH FROM (a.end_date - a.start_date)) / 3600)::numeric, 1)) " +
                        "FROM activities a " +
                        "WHERE va.id_activity = a.id_activity " +
                        "AND a.end_date < CURRENT_TIMESTAMP " +
                        "AND va.status = 'accepted'";

        jdbcTemplate.update(sqlVolunteers);

        String sqlActivities =
                "UPDATE activities " +
                        "SET status = 'completed' " +
                        "WHERE end_date < CURRENT_TIMESTAMP " +
                        "AND status != 'completed'";

        jdbcTemplate.update(sqlActivities);
    }

    public void save(Activity activity) {
        String sql = "INSERT INTO activities (id_category, id_coordinator, name, description, location, start_date, end_date, max_volunteers, status, target_donation) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'open', ?)";

        jdbcTemplate.update(sql,
                activity.getIdCategory(),
                activity.getIdCoordinator(),
                activity.getName(),
                activity.getDescription(),
                activity.getLocation(),
                activity.getStartDate(),
                activity.getEndDate(),
                activity.getMaxVolunteers(),
                activity.getTargetDonation());
    }

    public void updateStatus(Integer activityId, String status) {
        String sql = "UPDATE activities SET status = ? WHERE id_activity = ?";
        jdbcTemplate.update(sql, status, activityId);
    }

    public Optional<Activity> findById(Integer id) {
        String sql = "SELECT a.*, cat.name as category_name FROM activities a " +
                "LEFT JOIN activity_categories cat ON a.id_category = cat.id_category " +
                "WHERE a.id_activity = ?";
        List<Activity> results = jdbcTemplate.query(sql, activityRowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public void enrollVolunteer(Integer volunteerId, Integer activityId, String motivation) {
        String sql = "INSERT INTO volunteer_activities (id_volunteer, id_activity, enrollment_date, status, hours_completed, feedback) " +
                "VALUES (?, ?, CURRENT_DATE, 'pending', 0, ?)";
        jdbcTemplate.update(sql, volunteerId, activityId, motivation);
    }

    public boolean isEnrolled(Integer volunteerId, Integer activityId) {
        String sql = "SELECT COUNT(*) FROM volunteer_activities WHERE id_volunteer = ? AND id_activity = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, volunteerId, activityId);
        return count != null && count > 0;
    }

    public Map<String, Object> findMostPopularActivity() {
        String sql = "SELECT a.name, COUNT(va.id_volunteer) as vol_count " +
                "FROM activities a " +
                "JOIN volunteer_activities va ON a.id_activity = va.id_activity " +
                "GROUP BY a.name " +
                "ORDER BY vol_count DESC LIMIT 1";

        List<Map<String, Object>> result = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", rs.getString("name"));
            map.put("count", rs.getInt("vol_count"));
            return map;
        });
        return result.isEmpty() ? null : result.get(0);
    }

    public Double getTotalSystemDonations() {
        String sql = "SELECT SUM(target_donation) FROM activities";
        Double total = jdbcTemplate.queryForObject(sql, Double.class);
        return total != null ? total : 0.0;
    }

    public boolean hasOverlappingActivity(Integer coordinatorId, LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT COUNT(*) FROM activities " +
                "WHERE id_coordinator = ? " +
                "AND start_date < ? AND end_date > ? " +
                "AND status != 'completed'";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, coordinatorId, end, start);
        return count != null && count > 0;
    }

    public void updateStatusToInProgress(LocalDateTime now) {
        String sql = "UPDATE activities SET status = 'in_progress' " +
                "WHERE start_date <= ? AND end_date >= ? " +
                "AND status NOT IN ('in_progress', 'completed')";
        jdbcTemplate.update(sql, now, now);
    }

    public void updateStatusToCompleted(LocalDateTime now) {
        String sql = "UPDATE activities SET status = 'completed' " +
                "WHERE end_date < ? AND status != 'completed'";
        jdbcTemplate.update(sql, now);
    }
}