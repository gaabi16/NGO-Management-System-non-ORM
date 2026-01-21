package com.example.aplicatie_gestionare_voluntariat.repository;

import com.example.aplicatie_gestionare_voluntariat.model.Volunteer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class VolunteerRepository {

    private final JdbcTemplate jdbcTemplate;

    public VolunteerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Volunteer> volunteerRowMapper = new RowMapper<Volunteer>() {
        @Override
        public Volunteer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Volunteer volunteer = new Volunteer();
            volunteer.setIdVolunteer(rs.getInt("id_volunteer"));
            volunteer.setIdUser(rs.getInt("id_user"));

            if (rs.getDate("birth_date") != null) {
                volunteer.setBirthDate(rs.getDate("birth_date").toLocalDate());
            }
            volunteer.setSkills(rs.getString("skills"));
            volunteer.setAvailability(rs.getString("availability"));
            volunteer.setEmergencyContact(rs.getString("emergency_contact"));

            return volunteer;
        }
    };

    public void save(Volunteer volunteer) {
        String checkSql = "SELECT COUNT(*) FROM volunteers WHERE id_user = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, volunteer.getIdUser());

        if (count != null && count > 0) {
            String sql = "UPDATE volunteers SET birth_date = ?, skills = ?, availability = ?, emergency_contact = ? WHERE id_user = ?";
            jdbcTemplate.update(sql,
                    volunteer.getBirthDate(),
                    volunteer.getSkills(),
                    volunteer.getAvailability(),
                    volunteer.getEmergencyContact(),
                    volunteer.getIdUser());
        } else {
            String sql = "INSERT INTO volunteers (id_user, birth_date, skills, availability, emergency_contact) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql,
                    volunteer.getIdUser(),
                    volunteer.getBirthDate(),
                    volunteer.getSkills(),
                    volunteer.getAvailability(),
                    volunteer.getEmergencyContact());
        }
    }

    public Optional<Volunteer> findByUserId(Integer userId) {
        String sql = "SELECT * FROM volunteers WHERE id_user = ?";
        List<Volunteer> result = jdbcTemplate.query(sql, volunteerRowMapper, userId);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public boolean hasActiveOrPendingActivities(Integer userId) {
        String sql = "SELECT COUNT(*) FROM volunteer_activities va " +
                "JOIN volunteers v ON va.id_volunteer = v.id_volunteer " +
                "WHERE v.id_user = ? AND va.status IN ('pending', 'accepted')";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }

    public void deleteActivitiesByUserId(Integer userId) {
        String sql = "DELETE FROM volunteer_activities WHERE id_volunteer = " +
                "(SELECT id_volunteer FROM volunteers WHERE id_user = ?)";
        jdbcTemplate.update(sql, userId);
    }

    public void deleteByUserId(Integer userId) {
        String sql = "DELETE FROM volunteers WHERE id_user = ?";
        jdbcTemplate.update(sql, userId);
    }

    public Map<String, Object> findMostActiveVolunteer() {
        String sql = "SELECT u.first_name, u.last_name, COUNT(va.id_activity) as act_count " +
                "FROM volunteers v " +
                "JOIN users u ON v.id_user = u.id_user " +
                "JOIN volunteer_activities va ON v.id_volunteer = va.id_volunteer " +
                "GROUP BY u.first_name, u.last_name " +
                "ORDER BY act_count DESC LIMIT 1";

        List<Map<String, Object>> result = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", rs.getString("first_name") + " " + rs.getString("last_name"));
            map.put("count", rs.getInt("act_count"));
            return map;
        });

        return result.isEmpty() ? null : result.get(0);
    }

    public long count() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM volunteers", Long.class);
    }

    public List<Map<String, Object>> getLeaderboardData() {
        String sql =
                "SELECT * FROM (" +
                        "   SELECT " +
                        "       rank_table.full_name, " +
                        "       rank_table.id_volunteer, " +
                        "       ROUND(rank_table.total_weighted_score::numeric, 2) as score, " +
                        "       rank_table.activities_count, " +
                        "       DENSE_RANK() OVER (ORDER BY rank_table.total_weighted_score DESC) as global_rank, " +
                        "       ROUND((rank_table.total_weighted_score - " +
                        "           (SELECT AVG(total_weighted_score) FROM ( " +
                        "               SELECT SUM(GREATEST(COALESCE(hours_completed, 1), 1)) as total_weighted_score " +
                        "               FROM volunteer_activities " +
                        "               WHERE LOWER(status)='completed' " +
                        "               GROUP BY id_volunteer " +
                        "            ) avg_sub " +
                        "           ) " +
                        "       )::numeric, 1) as diff_from_avg, " +
                        "       CASE " +
                        "          WHEN rank_table.activities_count >= 10 THEN 'Elite' " +
                        "          WHEN rank_table.activities_count >= 5 THEN 'Veteran' " +
                        "          WHEN rank_table.activities_count >= 3 THEN 'Senior' " +
                        "          ELSE 'Junior' " +
                        "      END as rank_title " +
                        "   FROM ( " +
                        "       SELECT " +
                        "           u.first_name || ' ' || u.last_name as full_name, " +
                        "           v.id_volunteer, " +
                        "           COUNT(va.id_activity) as activities_count, " +
                        "           SUM( " +
                        "               GREATEST(COALESCE(va.hours_completed, 1), 1) * CASE " +
                        "                   WHEN va.enrollment_date > CURRENT_DATE - INTERVAL '30 days' THEN 1.5 " +
                        "                   WHEN va.enrollment_date > CURRENT_DATE - INTERVAL '90 days' THEN 1.2 " +
                        "                   ELSE 1.0 " +
                        "               END " +
                        "           ) as total_weighted_score " +
                        "       FROM volunteers v " +
                        "       JOIN users u ON v.id_user = u.id_user " +
                        "       JOIN volunteer_activities va ON v.id_volunteer = va.id_volunteer " +
                        "       WHERE LOWER(va.status) = 'completed' " +
                        "       GROUP BY u.first_name, u.last_name, v.id_volunteer " +
                        "   ) as rank_table " +
                        ") as final_ranking " +
                        "WHERE global_rank <= 10 " +
                        "ORDER BY global_rank ASC";

        return jdbcTemplate.queryForList(sql);
    }
}