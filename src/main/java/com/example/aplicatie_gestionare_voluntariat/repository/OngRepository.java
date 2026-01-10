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
            // INSERT
            String sql = "INSERT INTO ongs (registration_number, name, description, address, country, phone, email, founding_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql,
                    ong.getRegistrationNumber(),
                    ong.getName(),
                    ong.getDescription(),
                    ong.getAddress(),
                    ong.getCountry(),
                    ong.getPhone(),
                    ong.getEmail(),
                    ong.getFoundingDate()
            );
        } else {
            // UPDATE
            String sql = "UPDATE ongs SET name = ?, description = ?, address = ?, country = ?, phone = ?, email = ?, founding_date = ? " +
                    "WHERE registration_number = ?";
            jdbcTemplate.update(sql,
                    ong.getName(),
                    ong.getDescription(),
                    ong.getAddress(),
                    ong.getCountry(),
                    ong.getPhone(),
                    ong.getEmail(),
                    ong.getFoundingDate(),
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

    // [NOU] Metoda de filtrare avansata (Search + Country)
    public List<Ong> findFilteredOngs(String search, String country, int offset, int limit) {
        StringBuilder sql = new StringBuilder("SELECT * FROM ongs WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        // Cautare dupa Nume, Nr Inreg sau Email
        if (search != null && !search.trim().isEmpty()) {
            sql.append("AND (LOWER(name) LIKE LOWER(?) OR LOWER(registration_number) LIKE LOWER(?) OR LOWER(email) LIKE LOWER(?)) ");
            String searchPattern = "%" + search.trim() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        // Filtrare dupa tara
        if (country != null && !country.trim().isEmpty() && !country.equals("all")) {
            sql.append("AND LOWER(country) = LOWER(?) ");
            params.add(country.trim());
        }

        sql.append("ORDER BY name ASC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        return jdbcTemplate.query(sql.toString(), ongRowMapper, params.toArray());
    }

    // [NOU] Count pentru filtrare avansata
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

    // Metode legacy pastrate pentru compatibilitate (daca e cazul)
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
        String sql = "SELECT o.name, SUM(a.donated_amount) as total " +
                "FROM ongs o " +
                "JOIN coordinators c ON o.registration_number = c.ong_registration_number " +
                "JOIN activities a ON c.id_coordinator = a.id_coordinator " +
                "GROUP BY o.name " +
                "ORDER BY total DESC LIMIT 1";
        try {
            return Optional.of(jdbcTemplate.queryForMap(sql));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}