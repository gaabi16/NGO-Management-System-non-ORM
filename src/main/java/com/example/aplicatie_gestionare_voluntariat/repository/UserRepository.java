package com.example.aplicatie_gestionare_voluntariat.repository;

import com.example.aplicatie_gestionare_voluntariat.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<User> userRowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId_user(rs.getInt("id_user"));
            user.setEmail(rs.getString("email"));
            user.setPasswordHash(rs.getString("password_hash"));
            user.setFirst_name(rs.getString("first_name"));
            user.setLast_name(rs.getString("last_name"));
            user.setPhone_number(rs.getString("phone_number"));
            user.setRole(User.Role.valueOf(rs.getString("role")));
            return user;
        }
    };

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, email);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public User save(User user) {
        if (user.getId_user() == null) {
            // INSERT
            String sql = "INSERT INTO users (email, password_hash, first_name, last_name, phone_number, role) " +
                    "VALUES (?, ?, ?, ?, ?, ?::VARCHAR) RETURNING id_user";
            Integer id = jdbcTemplate.queryForObject(sql, Integer.class,
                    user.getEmail(),
                    user.getPasswordHash(),
                    user.getFirst_name(),
                    user.getLast_name(),
                    user.getPhone_number(),
                    user.getRole().name());
            user.setId_user(id);
        } else {
            // UPDATE
            String sql = "UPDATE users SET email = ?, password_hash = ?, first_name = ?, " +
                    "last_name = ?, phone_number = ?, role = ?::VARCHAR WHERE id_user = ?";
            jdbcTemplate.update(sql,
                    user.getEmail(),
                    user.getPasswordHash(),
                    user.getFirst_name(),
                    user.getLast_name(),
                    user.getPhone_number(),
                    user.getRole().name(),
                    user.getId_user());
        }
        return user;
    }

    public List<User> findAll(int limit, int offset) {
        String sql = "SELECT * FROM users ORDER BY id_user LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, userRowMapper, limit, offset);
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY id_user";
        return jdbcTemplate.query(sql, userRowMapper);
    }
}