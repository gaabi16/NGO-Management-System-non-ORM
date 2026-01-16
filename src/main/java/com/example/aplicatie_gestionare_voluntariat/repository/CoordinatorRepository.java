package com.example.aplicatie_gestionare_voluntariat.repository;

import com.example.aplicatie_gestionare_voluntariat.model.Coordinator;
import com.example.aplicatie_gestionare_voluntariat.model.Ong;
import com.example.aplicatie_gestionare_voluntariat.model.User;
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

            User user = new User();
            user.setIdUser(rs.getInt("id_user"));
            coordinator.setUser(user);

            Ong ong = new Ong();
            ong.setRegistrationNumber(rs.getString("ong_registration_number"));
            coordinator.setOng(ong);
            coordinator.setOngRegistrationNumber(rs.getString("ong_registration_number"));

            coordinator.setDepartment(rs.getString("department"));
            coordinator.setExperienceYears(rs.getInt("experience_years"));
            coordinator.setEmploymentType(rs.getString("employment_type"));

            return coordinator;
        }
    };

    public List<Coordinator> findFirst5() {
        String sql = "SELECT c.*, u.first_name, u.last_name, o.name as ong_name, o.registration_number " +
                "FROM coordinators c " +
                "JOIN users u ON c.id_user = u.id_user " +
                "JOIN ongs o ON c.ong_registration_number = o.registration_number " +
                "ORDER BY c.id_coordinator ASC LIMIT 5";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Coordinator c = new Coordinator();
            c.setIdCoordinator(rs.getInt("id_coordinator"));
            c.setDepartment(rs.getString("department"));
            c.setExperienceYears(rs.getInt("experience_years"));
            c.setEmploymentType(rs.getString("employment_type"));
            c.setOngRegistrationNumber(rs.getString("ong_registration_number"));

            User u = new User();
            u.setIdUser(rs.getInt("id_user"));
            u.setFirstName(rs.getString("first_name"));
            u.setLastName(rs.getString("last_name"));
            c.setUser(u);

            Ong o = new Ong();
            o.setRegistrationNumber(rs.getString("registration_number"));
            o.setName(rs.getString("ong_name"));
            c.setOng(o);

            return c;
        });
    }

    public Optional<Coordinator> findByUserId(Integer userId) {
        String sql = "SELECT * FROM coordinators WHERE id_user = ?";
        List<Coordinator> list = jdbcTemplate.query(sql, coordinatorRowMapper, userId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public void save(Coordinator coordinator) {
        String checkSql = "SELECT COUNT(*) FROM coordinators WHERE id_user = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, coordinator.getUser().getIdUser());

        if (count != null && count > 0) {
            String sql = "UPDATE coordinators SET ong_registration_number = ?, department = ?, experience_years = ?, employment_type = ? WHERE id_user = ?";
            jdbcTemplate.update(sql,
                    coordinator.getOngRegistrationNumber(),
                    coordinator.getDepartment(),
                    coordinator.getExperienceYears(),
                    coordinator.getEmploymentType(),
                    coordinator.getUser().getIdUser());
        } else {
            String sql = "INSERT INTO coordinators (id_user, ong_registration_number, department, experience_years, employment_type) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql,
                    coordinator.getUser().getIdUser(),
                    coordinator.getOngRegistrationNumber(),
                    coordinator.getDepartment(),
                    coordinator.getExperienceYears(),
                    coordinator.getEmploymentType());
        }
    }

    public void deleteByUserId(Integer userId) {
        String sql = "DELETE FROM coordinators WHERE id_user = ?";
        jdbcTemplate.update(sql, userId);
    }

    public long count() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM coordinators", Long.class);
    }

    public Map<String, Object> findTopCoordinator() {
        String sql = "SELECT u.first_name, u.last_name, COUNT(a.id_activity) as act_count " +
                "FROM coordinators c " +
                "JOIN users u ON c.id_user = u.id_user " +
                "JOIN activities a ON c.id_coordinator = a.id_coordinator " +
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
}