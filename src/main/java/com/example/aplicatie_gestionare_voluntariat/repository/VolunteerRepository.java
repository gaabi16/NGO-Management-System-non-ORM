package com.example.aplicatie_gestionare_voluntariat.repository;

import com.example.aplicatie_gestionare_voluntariat.model.Volunteer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
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

    // [NOU] Verifică dacă există activități pending sau accepted pentru un user
    public boolean hasActiveOrPendingActivities(Integer userId) {
        String sql = "SELECT COUNT(*) FROM volunteer_activities va " +
                "JOIN volunteers v ON va.id_volunteer = v.id_volunteer " +
                "WHERE v.id_user = ? AND va.status IN ('pending', 'accepted')";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }

    // [NOU] Șterge toate înscrierile unui voluntar (pentru a permite ștergerea profilului)
    public void deleteActivitiesByUserId(Integer userId) {
        String sql = "DELETE FROM volunteer_activities WHERE id_volunteer = " +
                "(SELECT id_volunteer FROM volunteers WHERE id_user = ?)";
        jdbcTemplate.update(sql, userId);
    }

    public void deleteByUserId(Integer userId) {
        String sql = "DELETE FROM volunteers WHERE id_user = ?";
        jdbcTemplate.update(sql, userId);
    }
}