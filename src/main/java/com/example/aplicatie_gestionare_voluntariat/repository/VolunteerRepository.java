package com.example.aplicatie_gestionare_voluntariat.repository;

import com.example.aplicatie_gestionare_voluntariat.model.Volunteer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
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

            Date birthDate = rs.getDate("birth_date");
            if (birthDate != null) {
                volunteer.setBirthDate(birthDate.toLocalDate());
            }

            volunteer.setSkills(rs.getString("skills"));
            volunteer.setAvailability(rs.getString("availability"));
            volunteer.setEmergencyContact(rs.getString("emergency_contact"));

            return volunteer;
        }
    };

    public Volunteer save(Volunteer volunteer) {
        if (volunteer.getIdVolunteer() == null) {
            // INSERT
            String sql = "INSERT INTO volunteers (id_user, birth_date, skills, availability, emergency_contact) " +
                    "VALUES (?, ?, ?, ?, ?) RETURNING id_volunteer";
            Integer id = jdbcTemplate.queryForObject(sql, Integer.class,
                    volunteer.getIdUser(),
                    volunteer.getBirthDate(),
                    volunteer.getSkills(),
                    volunteer.getAvailability(),
                    volunteer.getEmergencyContact());
            volunteer.setIdVolunteer(id);
        } else {
            // UPDATE
            String sql = "UPDATE volunteers SET birth_date = ?, skills = ?, " +
                    "availability = ?, emergency_contact = ? WHERE id_volunteer = ?";
            jdbcTemplate.update(sql,
                    volunteer.getBirthDate(),
                    volunteer.getSkills(),
                    volunteer.getAvailability(),
                    volunteer.getEmergencyContact(),
                    volunteer.getIdVolunteer());
        }
        return volunteer;
    }

    public Optional<Volunteer> findByUserId(Integer userId) {
        String sql = "SELECT * FROM volunteers WHERE id_user = ?";
        List<Volunteer> volunteers = jdbcTemplate.query(sql, volunteerRowMapper, userId);
        return volunteers.isEmpty() ? Optional.empty() : Optional.of(volunteers.get(0));
    }

    public Optional<Volunteer> findById(Integer id) {
        String sql = "SELECT * FROM volunteers WHERE id_volunteer = ?";
        List<Volunteer> volunteers = jdbcTemplate.query(sql, volunteerRowMapper, id);
        return volunteers.isEmpty() ? Optional.empty() : Optional.of(volunteers.get(0));
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM volunteers WHERE id_volunteer = ?";
        jdbcTemplate.update(sql, id);
    }

    public void deleteByUserId(Integer userId) {
        String sql = "DELETE FROM volunteers WHERE id_user = ?";
        jdbcTemplate.update(sql, userId);
    }
}