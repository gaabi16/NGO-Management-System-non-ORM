package com.example.aplicatie_gestionare_voluntariat.repository;

import com.example.aplicatie_gestionare_voluntariat.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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

    // [MODIFICAT] Logica de cautare pentru a include concatenarea numelui
    public List<User> findFilteredUsers(String search, List<User.Role> roles, int offset, int limit) {
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.trim().isEmpty()) {
            // Cautam in Prenume SAU Nume SAU "Prenume Nume" (concatenat) SAU Email
            sql.append("AND (LOWER(first_name) LIKE LOWER(?) OR LOWER(last_name) LIKE LOWER(?) OR LOWER(first_name || ' ' || last_name) LIKE LOWER(?) OR LOWER(email) LIKE LOWER(?)) ");
            String searchPattern = "%" + search.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern); // pentru concatenare
            params.add(searchPattern); // pentru email
        }

        if (roles != null && !roles.isEmpty()) {
            sql.append("AND role IN (");
            for (int i = 0; i < roles.size(); i++) {
                sql.append(i > 0 ? ",?" : "?");
                params.add(roles.get(i).name());
            }
            sql.append(") ");
        }

        sql.append("ORDER BY id_user ASC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        return jdbcTemplate.query(sql.toString(), userRowMapper, params.toArray());
    }

    // [MODIFICAT] Acelasi fix si pentru count (necesar pentru paginare)
    public long countFilteredUsers(String search, List<User.Role> roles) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM users WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (LOWER(first_name) LIKE LOWER(?) OR LOWER(last_name) LIKE LOWER(?) OR LOWER(first_name || ' ' || last_name) LIKE LOWER(?) OR LOWER(email) LIKE LOWER(?)) ");
            String searchPattern = "%" + search.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        if (roles != null && !roles.isEmpty()) {
            sql.append("AND role IN (");
            for (int i = 0; i < roles.size(); i++) {
                sql.append(i > 0 ? ",?" : "?");
                params.add(roles.get(i).name());
            }
            sql.append(") ");
        }

        return jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
    }

    // Metode legacy
    public List<User> findAllPaginated(int offset, int limit) {
        return findFilteredUsers(null, null, offset, limit);
    }
    public long count() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Long.class);
    }
    public long countByRoleIn(List<User.Role> roles) {
        return countFilteredUsers(null, roles);
    }
    public List<User> findByRoleInPaginated(List<User.Role> roles, int offset, int limit) {
        return findFilteredUsers(null, roles, offset, limit);
    }
}