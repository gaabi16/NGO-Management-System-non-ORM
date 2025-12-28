package com.example.aplicatie_gestionare_voluntariat.repository;

import com.example.aplicatie_gestionare_voluntariat.model.Coordinator;
import com.example.aplicatie_gestionare_voluntariat.model.Ong;
import com.example.aplicatie_gestionare_voluntariat.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CoordinatorRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Coordinator> coordinatorRowMapper = new RowMapper<Coordinator>() {
        @Override
        public Coordinator mapRow(ResultSet rs, int rowNum) throws SQLException {
            Coordinator coordinator = new Coordinator();
            coordinator.setIdCoordinator(rs.getInt("id_coordinator"));
            coordinator.setIdUser(rs.getInt("id_user"));
            coordinator.setIdOng(rs.getInt("id_ong"));
            coordinator.setDepartment(rs.getString("department"));

            int experienceYears = rs.getInt("experience_years");
            if (!rs.wasNull()) {
                coordinator.setExperienceYears(experienceYears);
            }

            coordinator.setEmploymentType(rs.getString("employment_type"));

            // Map User dacă există coloane
            try {
                User user = new User();
                user.setId_user(rs.getInt("user_id"));
                user.setFirst_name(rs.getString("first_name"));
                user.setLast_name(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                coordinator.setUser(user);
            } catch (SQLException e) {
                // Coloanele user nu există în acest query
            }

            // Map Ong dacă există coloane
            try {
                Ong ong = new Ong();
                ong.setIdOng(rs.getInt("ong_id"));
                ong.setName(rs.getString("ong_name"));
                coordinator.setOng(ong);
            } catch (SQLException e) {
                // Coloanele ong nu există în acest query
            }

            return coordinator;
        }
    };

    public List<Coordinator> findAll(int limit, int offset) {
        String sql = "SELECT c.*, " +
                "u.id_user as user_id, u.first_name, u.last_name, u.email, " +
                "o.id_ong as ong_id, o.name as ong_name " +
                "FROM coordinators c " +
                "LEFT JOIN users u ON c.id_user = u.id_user " +
                "LEFT JOIN ongs o ON c.id_ong = o.id_ong " +
                "ORDER BY c.id_coordinator LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, coordinatorRowMapper, limit, offset);
    }

    public List<Coordinator> findAll() {
        String sql = "SELECT c.*, " +
                "u.id_user as user_id, u.first_name, u.last_name, u.email, " +
                "o.id_ong as ong_id, o.name as ong_name " +
                "FROM coordinators c " +
                "LEFT JOIN users u ON c.id_user = u.id_user " +
                "LEFT JOIN ongs o ON c.id_ong = o.id_ong " +
                "ORDER BY c.id_coordinator";
        return jdbcTemplate.query(sql, coordinatorRowMapper);
    }

    public Coordinator save(Coordinator coordinator) {
        if (coordinator.getIdCoordinator() == null) {
            // INSERT
            String sql = "INSERT INTO coordinators (id_user, id_ong, department, experience_years, employment_type) " +
                    "VALUES (?, ?, ?, ?, ?) RETURNING id_coordinator";
            Integer id = jdbcTemplate.queryForObject(sql, Integer.class,
                    coordinator.getIdUser(),
                    coordinator.getIdOng(),
                    coordinator.getDepartment(),
                    coordinator.getExperienceYears(),
                    coordinator.getEmploymentType());
            coordinator.setIdCoordinator(id);
        } else {
            // UPDATE
            String sql = "UPDATE coordinators SET id_user = ?, id_ong = ?, department = ?, " +
                    "experience_years = ?, employment_type = ? WHERE id_coordinator = ?";
            jdbcTemplate.update(sql,
                    coordinator.getIdUser(),
                    coordinator.getIdOng(),
                    coordinator.getDepartment(),
                    coordinator.getExperienceYears(),
                    coordinator.getEmploymentType(),
                    coordinator.getIdCoordinator());
        }
        return coordinator;
    }
}