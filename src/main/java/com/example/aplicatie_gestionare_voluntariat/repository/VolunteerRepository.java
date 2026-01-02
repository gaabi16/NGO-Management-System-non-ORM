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

            // Mapare câmpuri noi (pot fi null în DB, deci verificăm)
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
        // Verificăm dacă există deja o intrare
        String checkSql = "SELECT COUNT(*) FROM volunteers WHERE id_user = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, volunteer.getIdUser());

        if (count != null && count > 0) {
            // UPDATE
            String sql = "UPDATE volunteers SET birth_date = ?, skills = ?, availability = ?, emergency_contact = ? WHERE id_user = ?";
            jdbcTemplate.update(sql,
                    volunteer.getBirthDate(),
                    volunteer.getSkills(),
                    volunteer.getAvailability(),
                    volunteer.getEmergencyContact(),
                    volunteer.getIdUser());
        } else {
            // INSERT
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

    public void deleteByUserId(Integer userId) {
        String sql = "DELETE FROM volunteers WHERE id_user = ?";
        jdbcTemplate.update(sql, userId);
    }
}