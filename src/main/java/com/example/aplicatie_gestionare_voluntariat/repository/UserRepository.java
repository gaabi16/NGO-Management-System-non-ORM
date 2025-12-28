package com.example.aplicatie_gestionare_voluntariat.repository;

import com.example.aplicatie_gestionare_voluntariat.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<User> userRowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setIdUser(rs.getInt("id_user"));
            user.setEmail(rs.getString("email"));
            user.setPasswordHash(rs.getString("password_hash"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setPhoneNumber(rs.getString("phone_number"));
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
        if (user.getIdUser() == null) {
            // INSERT
            String sql = "INSERT INTO users (email, password_hash, first_name, last_name, phone_number, role) " +
                    "VALUES (?, ?, ?, ?, ?, ?::VARCHAR)";

            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getPasswordHash());
                ps.setString(3, user.getFirstName());
                ps.setString(4, user.getLastName());
                ps.setString(5, user.getPhoneNumber());
                ps.setString(6, user.getRole().name());
                return ps;
            }, keyHolder);

            user.setIdUser(((Number) keyHolder.getKeys().get("id_user")).intValue());
        } else {
            // UPDATE
            String sql = "UPDATE users SET email = ?, password_hash = ?, first_name = ?, " +
                    "last_name = ?, phone_number = ?, role = ?::VARCHAR WHERE id_user = ?";
            jdbcTemplate.update(sql,
                    user.getEmail(),
                    user.getPasswordHash(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getPhoneNumber(),
                    user.getRole().name(),
                    user.getIdUser()
            );
        }
        return user;
    }

    public Optional<User> findById(Integer id) {
        String sql = "SELECT * FROM users WHERE id_user = ?";
        List<User> users = jdbcTemplate.query(sql, userRowMapper, id);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    public void deleteById(Integer id) {
        String sql = "DELETE FROM users WHERE id_user = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<User> findAllPaginated(int offset, int limit) {
        String sql = "SELECT * FROM users ORDER BY id_user ASC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, userRowMapper, limit, offset);
    }

    public List<User> findByRoleInPaginated(List<User.Role> roles, int offset, int limit) {
        if (roles == null || roles.isEmpty()) {
            return findAllPaginated(offset, limit);
        }

        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE role IN (");
        for (int i = 0; i < roles.size(); i++) {
            sql.append(i > 0 ? ",?" : "?");
        }
        sql.append(") ORDER BY id_user ASC LIMIT ? OFFSET ?");

        Object[] params = new Object[roles.size() + 2];
        for (int i = 0; i < roles.size(); i++) {
            params[i] = roles.get(i).name();
        }
        params[roles.size()] = limit;
        params[roles.size() + 1] = offset;

        return jdbcTemplate.query(sql.toString(), userRowMapper, params);
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM users";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public long countByRoleIn(List<User.Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return count();
        }

        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM users WHERE role IN (");
        for (int i = 0; i < roles.size(); i++) {
            sql.append(i > 0 ? ",?" : "?");
        }
        sql.append(")");

        Object[] params = new Object[roles.size()];
        for (int i = 0; i < roles.size(); i++) {
            params[i] = roles.get(i).name();
        }

        return jdbcTemplate.queryForObject(sql.toString(), Long.class, params);
    }
}