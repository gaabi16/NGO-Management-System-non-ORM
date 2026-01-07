package com.example.aplicatie_gestionare_voluntariat.repository;

import com.example.aplicatie_gestionare_voluntariat.model.Activity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
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
            activity.setDonationsCollected(rs.getDouble("donations_collected"));

            try {
                activity.setCategoryName(rs.getString("category_name"));
            } catch (SQLException e) { /* ignore if column missing */ }

            // [NOU] Încercăm să mapăm pending_count dacă există în query
            try {
                activity.setPendingCount(rs.getInt("pending_count"));
            } catch (SQLException e) {
                activity.setPendingCount(0);
            }

            return activity;
        }
    };

    public List<Activity> findByOngId(String ongRegistrationNumber) {
        String sql = "SELECT a.*, cat.name as category_name " +
                "FROM activities a " +
                "JOIN coordinators c ON a.id_coordinator = c.id_coordinator " +
                "LEFT JOIN activity_categories cat ON a.id_category = cat.id_category " +
                "WHERE c.ong_registration_number = ? " +
                "ORDER BY a.start_date DESC";
        return jdbcTemplate.query(sql, activityRowMapper, ongRegistrationNumber);
    }

    // [MODIFICAT] Include subquery pentru pending_count
    public List<Activity> findByCoordinatorId(Integer coordinatorId) {
        String sql = "SELECT a.*, cat.name as category_name, " +
                "(SELECT COUNT(*) FROM volunteer_activities va WHERE va.id_activity = a.id_activity AND va.status = 'pending') as pending_count " +
                "FROM activities a " +
                "LEFT JOIN activity_categories cat ON a.id_category = cat.id_category " +
                "WHERE a.id_coordinator = ? " +
                "ORDER BY a.start_date DESC";
        return jdbcTemplate.query(sql, activityRowMapper, coordinatorId);
    }

    public void save(Activity activity) {
        String sql = "INSERT INTO activities (id_category, id_coordinator, name, description, location, start_date, end_date, max_volunteers, status, donations_collected) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'open', 0.0)";

        jdbcTemplate.update(sql,
                activity.getIdCategory(),
                activity.getIdCoordinator(),
                activity.getName(),
                activity.getDescription(),
                activity.getLocation(),
                activity.getStartDate(),
                activity.getEndDate(),
                activity.getMaxVolunteers());
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
}