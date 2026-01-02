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

            // Mapăm obiectele imbricate (User și Ong) sumar, doar ID-urile sunt critice aici
            // Într-o implementare completă am face join-uri, dar aici ne interesează datele de coordonator
            User user = new User();
            user.setIdUser(rs.getInt("id_user"));
            coordinator.setUser(user);

            Ong ong = new Ong();
            ong.setIdOng(rs.getInt("id_ong"));
            coordinator.setOng(ong);

            coordinator.setDepartment(rs.getString("department"));
            coordinator.setExperienceYears(rs.getInt("experience_years"));
            coordinator.setEmploymentType(rs.getString("employment_type"));

            return coordinator;
        }
    };

    public List<Coordinator> findFirst5() {
        // Aici facem JOIN pentru a afișa date frumoase în dashboard
        String sql = "SELECT c.*, u.first_name, u.last_name, o.name as ong_name " +
                "FROM coordinators c " +
                "JOIN users u ON c.id_user = u.id_user " +
                "JOIN ongs o ON c.id_ong = o.id_ong " +
                "ORDER BY c.id_coordinator ASC LIMIT 5";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Coordinator c = new Coordinator();
            c.setIdCoordinator(rs.getInt("id_coordinator"));
            c.setDepartment(rs.getString("department"));
            c.setExperienceYears(rs.getInt("experience_years"));
            c.setEmploymentType(rs.getString("employment_type"));

            User u = new User();
            u.setIdUser(rs.getInt("id_user"));
            u.setFirstName(rs.getString("first_name"));
            u.setLastName(rs.getString("last_name"));
            c.setUser(u);

            Ong o = new Ong();
            o.setIdOng(rs.getInt("id_ong"));
            o.setName(rs.getString("ong_name"));
            c.setOng(o);

            return c;
        });
    }

    // --- METODE NOI PENTRU CRUD ---

    public Optional<Coordinator> findByUserId(Integer userId) {
        String sql = "SELECT * FROM coordinators WHERE id_user = ?";
        List<Coordinator> list = jdbcTemplate.query(sql, coordinatorRowMapper, userId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public void save(Coordinator coordinator) {
        // Verificăm dacă există deja o intrare pentru acest user
        String checkSql = "SELECT COUNT(*) FROM coordinators WHERE id_user = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, coordinator.getUser().getIdUser());

        if (count != null && count > 0) {
            // UPDATE
            String sql = "UPDATE coordinators SET id_ong = ?, department = ?, experience_years = ?, employment_type = ? WHERE id_user = ?";
            jdbcTemplate.update(sql,
                    coordinator.getOng().getIdOng(),
                    coordinator.getDepartment(),
                    coordinator.getExperienceYears(),
                    coordinator.getEmploymentType(),
                    coordinator.getUser().getIdUser());
        } else {
            // INSERT
            String sql = "INSERT INTO coordinators (id_user, id_ong, department, experience_years, employment_type) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql,
                    coordinator.getUser().getIdUser(),
                    coordinator.getOng().getIdOng(),
                    coordinator.getDepartment(),
                    coordinator.getExperienceYears(),
                    coordinator.getEmploymentType());
        }
    }

    public void deleteByUserId(Integer userId) {
        String sql = "DELETE FROM coordinators WHERE id_user = ?";
        jdbcTemplate.update(sql, userId);
    }
}