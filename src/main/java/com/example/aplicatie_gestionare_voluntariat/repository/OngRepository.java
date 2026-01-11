package com.example.aplicatie_gestionare_voluntariat.repository;

import com.example.aplicatie_gestionare_voluntariat.model.Ong;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
            if (rs.getDate("founding_date") != null) {
                ong.setFoundingDate(rs.getDate("founding_date").toLocalDate());
            }
            // Mapam imaginea
            ong.setImageUrl(rs.getString("image_url"));
            return ong;
        }
    };

    public Optional<Ong> findById(String registrationNumber) {
        String sql = "SELECT * FROM ongs WHERE registration_number = ?";
        List<Ong> ongs = jdbcTemplate.query(sql, ongRowMapper, registrationNumber);
        return ongs.isEmpty() ? Optional.empty() : Optional.of(ongs.get(0));
    }

    public Ong save(Ong ong) {
        Optional<Ong> existing = findById(ong.getRegistrationNumber());
        if (existing.isEmpty()) {
            // INSERT - includem image_url
            String sql = "INSERT INTO ongs (registration_number, name, description, address, country, phone, email, founding_date, image_url) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql,
                    ong.getRegistrationNumber(),
                    ong.getName(),
                    ong.getDescription(),
                    ong.getAddress(),
                    ong.getCountry(),
                    ong.getPhone(),
                    ong.getEmail(),
                    ong.getFoundingDate(),
                    ong.getImageUrl()
            );
        } else {
            // UPDATE - includem image_url
            String sql = "UPDATE ongs SET name = ?, description = ?, address = ?, country = ?, phone = ?, email = ?, founding_date = ?, image_url = ? " +
                    "WHERE registration_number = ?";
            jdbcTemplate.update(sql,
                    ong.getName(),
                    ong.getDescription(),
                    ong.getAddress(),
                    ong.getCountry(),
                    ong.getPhone(),
                    ong.getEmail(),
                    ong.getFoundingDate(),
                    ong.getImageUrl(),
                    ong.getRegistrationNumber()
            );
        }
        return ong;
    }

    public void deleteById(String registrationNumber) {
        String sql = "DELETE FROM ongs WHERE registration_number = ?";
        jdbcTemplate.update(sql, registrationNumber);
    }

    public List<Ong> findAll() {
        return jdbcTemplate.query("SELECT * FROM ongs", ongRowMapper);
    }

    public List<Ong> findFirst5() {
        return jdbcTemplate.query("SELECT * FROM ongs ORDER BY founding_date DESC LIMIT 5", ongRowMapper);
    }

    public List<Ong> findFilteredOngs(String search, String country, int offset, int limit) {
        StringBuilder sql = new StringBuilder("SELECT * FROM ongs WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (LOWER(name) LIKE LOWER(?) OR LOWER(registration_number) LIKE LOWER(?) OR LOWER(email) LIKE LOWER(?)) ");
            String searchPattern = "%" + search.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        if (country != null && !country.trim().isEmpty() && !country.equals("all")) {
            sql.append("AND LOWER(country) = LOWER(?) ");
            params.add(country.trim());
        }

        sql.append("ORDER BY name ASC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        return jdbcTemplate.query(sql.toString(), ongRowMapper, params.toArray());
    }

    public long countFilteredOngs(String search, String country) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM ongs WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (LOWER(name) LIKE LOWER(?) OR LOWER(registration_number) LIKE LOWER(?) OR LOWER(email) LIKE LOWER(?)) ");
            String searchPattern = "%" + search.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        if (country != null && !country.trim().isEmpty() && !country.equals("all")) {
            sql.append("AND LOWER(country) = LOWER(?) ");
            params.add(country.trim());
        }

        return jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
    }

    public List<Ong> findAll(int limit, int offset) {
        return jdbcTemplate.query("SELECT * FROM ongs ORDER BY name ASC LIMIT ? OFFSET ?", ongRowMapper, limit, offset);
    }

    public long count() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ongs", Long.class);
    }

    public List<Ong> findByRegistrationNumberPaginated(String regNum, int limit, int offset) {
        String sql = "SELECT * FROM ongs WHERE registration_number LIKE ? ORDER BY registration_number LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, ongRowMapper, "%" + regNum + "%", limit, offset);
    }

    public long countByRegistrationNumber(String regNum) {
        String sql = "SELECT COUNT(*) FROM ongs WHERE registration_number LIKE ?";
        return jdbcTemplate.queryForObject(sql, Long.class, "%" + regNum + "%");
    }

    public Optional<java.util.Map<String, Object>> findTopFundraisingOng() {
        String sql = "SELECT o.name, SUM(d.amount) as total " +
                "FROM ongs o " +
                "JOIN donations d ON o.registration_number = d.ong_registration_number " +
                "GROUP BY o.name " +
                "ORDER BY total DESC LIMIT 1";
        try {
            return Optional.of(jdbcTemplate.queryForMap(sql));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}