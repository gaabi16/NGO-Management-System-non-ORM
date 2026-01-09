package com.example.aplicatie_gestionare_voluntariat.repository;

import com.example.aplicatie_gestionare_voluntariat.model.Ong;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class OngRepository {

    private final JdbcTemplate jdbcTemplate;

    public OngRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Ong> ongRowMapper = new RowMapper<Ong>() {
        @Override
        public Ong mapRow(ResultSet rs, int rowNum) throws SQLException {
            Ong ong = new Ong();
            ong.setRegistrationNumber(rs.getString("registration_number"));
            ong.setName(rs.getString("name"));
            ong.setDescription(rs.getString("description"));
            ong.setAddress(rs.getString("address"));
            ong.setCountry(rs.getString("country"));
            ong.setPhone(rs.getString("phone"));
            ong.setEmail(rs.getString("email"));
            Date foundingDate = rs.getDate("founding_date");
            if (foundingDate != null) {
                ong.setFoundingDate(foundingDate.toLocalDate());
            }
            return ong;
        }
    };

    public List<Ong> findFirst5() {
        String sql = "SELECT * FROM ongs ORDER BY registration_number ASC LIMIT 5";
        return jdbcTemplate.query(sql, ongRowMapper);
    }

    public List<Ong> findAll(int limit, int offset) {
        String sql = "SELECT * FROM ongs ORDER BY registration_number ASC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, ongRowMapper, limit, offset);
    }

    public List<Ong> findAll() {
        String sql = "SELECT * FROM ongs ORDER BY registration_number";
        return jdbcTemplate.query(sql, ongRowMapper);
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM ongs";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public List<Ong> findByRegistrationNumberPaginated(String regNumber, int limit, int offset) {
        String sql = "SELECT * FROM ongs WHERE LOWER(registration_number) LIKE LOWER(?) ORDER BY registration_number ASC LIMIT ? OFFSET ?";
        String searchPattern = "%" + regNumber + "%";
        return jdbcTemplate.query(sql, ongRowMapper, searchPattern, limit, offset);
    }

    public long countByRegistrationNumber(String regNumber) {
        String sql = "SELECT COUNT(*) FROM ongs WHERE LOWER(registration_number) LIKE LOWER(?)";
        String searchPattern = "%" + regNumber + "%";
        return jdbcTemplate.queryForObject(sql, Long.class, searchPattern);
    }

    public Optional<Ong> findById(String registrationNumber) {
        String sql = "SELECT * FROM ongs WHERE registration_number = ?";
        List<Ong> ongs = jdbcTemplate.query(sql, ongRowMapper, registrationNumber);
        return ongs.isEmpty() ? Optional.empty() : Optional.of(ongs.get(0));
    }

    public void deleteById(String registrationNumber) {
        String sql = "DELETE FROM ongs WHERE registration_number = ?";
        jdbcTemplate.update(sql, registrationNumber);
    }

    public Ong save(Ong ong) {
        String checkSql = "SELECT COUNT(*) FROM ongs WHERE registration_number = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, ong.getRegistrationNumber());

        if (count == null || count == 0) {
            String sql = "INSERT INTO ongs (registration_number, name, description, address, country, phone, email, founding_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql, ong.getRegistrationNumber(), ong.getName(), ong.getDescription(), ong.getAddress(),
                    ong.getCountry(), ong.getPhone(), ong.getEmail(), ong.getFoundingDate());
        } else {
            String sql = "UPDATE ongs SET name = ?, description = ?, address = ?, country = ?, " +
                    "phone = ?, email = ?, founding_date = ? WHERE registration_number = ?";
            jdbcTemplate.update(sql, ong.getName(), ong.getDescription(), ong.getAddress(), ong.getCountry(),
                    ong.getPhone(), ong.getEmail(), ong.getFoundingDate(), ong.getRegistrationNumber());
        }
        return ong;
    }

    // [NOU] Returnează ONG-ul cu cele mai multe fonduri strânse din activități
    public Map<String, Object> findTopFundraisingOng() {
        String sql = "SELECT o.name, SUM(a.donations_collected) as total " +
                "FROM ongs o " +
                "JOIN coordinators c ON o.registration_number = c.ong_registration_number " +
                "JOIN activities a ON c.id_coordinator = a.id_coordinator " +
                "GROUP BY o.name " +
                "ORDER BY total DESC LIMIT 1";

        List<Map<String, Object>> result = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", rs.getString("name"));
            map.put("total", rs.getDouble("total"));
            return map;
        });

        return result.isEmpty() ? null : result.get(0);
    }
}