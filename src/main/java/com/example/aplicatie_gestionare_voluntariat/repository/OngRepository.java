package com.example.aplicatie_gestionare_voluntariat.repository;

import com.example.aplicatie_gestionare_voluntariat.model.Ong;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
            ong.setIdOng(rs.getInt("id_ong"));
            ong.setName(rs.getString("name"));
            ong.setDescription(rs.getString("description"));
            ong.setAddress(rs.getString("address"));
            ong.setRegistrationNumber(rs.getString("registration_number"));
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
        String sql = "SELECT * FROM ongs ORDER BY id_ong ASC LIMIT 5";
        return jdbcTemplate.query(sql, ongRowMapper);
    }

    // Această metodă exista deja, o folosim pentru paginare
    public List<Ong> findAll(int limit, int offset) {
        String sql = "SELECT * FROM ongs ORDER BY id_ong ASC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, ongRowMapper, limit, offset);
    }

    public List<Ong> findAll() {
        String sql = "SELECT * FROM ongs ORDER BY id_ong";
        return jdbcTemplate.query(sql, ongRowMapper);
    }

    // Metoda nouă necesară pentru calculul paginilor
    public long count() {
        String sql = "SELECT COUNT(*) FROM ongs";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public Ong save(Ong ong) {
        if (ong.getIdOng() == null) {
            // INSERT
            String sql = "INSERT INTO ongs (name, description, address, registration_number, phone, email, founding_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id_ong";
            Integer id = jdbcTemplate.queryForObject(sql, Integer.class,
                    ong.getName(),
                    ong.getDescription(),
                    ong.getAddress(),
                    ong.getRegistrationNumber(),
                    ong.getPhone(),
                    ong.getEmail(),
                    ong.getFoundingDate());
            ong.setIdOng(id);
        } else {
            // UPDATE
            String sql = "UPDATE ongs SET name = ?, description = ?, address = ?, " +
                    "registration_number = ?, phone = ?, email = ?, founding_date = ? WHERE id_ong = ?";
            jdbcTemplate.update(sql,
                    ong.getName(),
                    ong.getDescription(),
                    ong.getAddress(),
                    ong.getRegistrationNumber(),
                    ong.getPhone(),
                    ong.getEmail(),
                    ong.getFoundingDate(),
                    ong.getIdOng());
        }
        return ong;
    }
}