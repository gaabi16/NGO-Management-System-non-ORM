package com.example.aplicatie_gestionare_voluntariat.repository;

import com.example.aplicatie_gestionare_voluntariat.model.Coordinator;
import com.example.aplicatie_gestionare_voluntariat.model.Ong;
import com.example.aplicatie_gestionare_voluntariat.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CoordinatorRepository {

    private final JdbcTemplate jdbcTemplate;

    public CoordinatorRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Coordinator> coordinatorRowMapper = new RowMapper<Coordinator>() {
        @Override
        public Coordinator mapRow(ResultSet rs, int rowNum) throws SQLException {
            Coordinator coordinator = new Coordinator();
            coordinator.setIdCoordinator(rs.getInt("id_coordinator"));
            coordinator.setIdUser(rs.getInt("id_user"));
            coordinator.setIdOng(rs.getInt("id_ong"));
            coordinator.setDepartment(rs.getString("department"));

            Integer expYears = (Integer) rs.getObject("experience_years");
            coordinator.setExperienceYears(expYears);

            coordinator.setEmploymentType(rs.getString("employment_type"));

            // Mapare User (din JOIN)
            try {
                User user = new User();
                user.setIdUser(rs.getInt("user_id"));
                user.setEmail(rs.getString("user_email"));
                user.setFirstName(rs.getString("user_first_name"));
                user.setLastName(rs.getString("user_last_name"));
                user.setPhoneNumber(rs.getString("user_phone_number"));
                user.setRole(User.Role.valueOf(rs.getString("user_role")));
                coordinator.setUser(user);
            } catch (Exception e) {
                // Dacă nu există datele user în ResultSet, setăm null
                coordinator.setUser(null);
            }

            // Mapare Ong (din JOIN)
            try {
                Ong ong = new Ong();
                ong.setIdOng(rs.getInt("ong_id"));
                ong.setName(rs.getString("ong_name"));
                ong.setEmail(rs.getString("ong_email"));
                ong.setPhone(rs.getString("ong_phone"));
                coordinator.setOng(ong);
            } catch (Exception e) {
                // Dacă nu există datele ong în ResultSet, setăm null
                coordinator.setOng(null);
            }

            return coordinator;
        }
    };

    public List<Coordinator> findFirst5() {
        String sql = "SELECT " +
                "c.id_coordinator, c.id_user, c.id_ong, c.department, " +
                "c.experience_years, c.employment_type, " +
                "u.id_user as user_id, u.email as user_email, u.first_name as user_first_name, " +
                "u.last_name as user_last_name, u.phone_number as user_phone_number, u.role as user_role, " +
                "o.id_ong as ong_id, o.name as ong_name, o.email as ong_email, o.phone as ong_phone " +
                "FROM coordinators c " +
                "LEFT JOIN users u ON c.id_user = u.id_user " +
                "LEFT JOIN ongs o ON c.id_ong = o.id_ong " +
                "ORDER BY c.id_coordinator ASC LIMIT 5";

        return jdbcTemplate.query(sql, coordinatorRowMapper);
    }
}