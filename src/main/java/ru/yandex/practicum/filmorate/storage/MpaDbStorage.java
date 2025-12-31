package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Repository
@Qualifier("mpaDbStorage")
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Mpa> mpaRowMapper = (rs, rowNum) -> {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa_id"));
        String name = rs.getString("name");
        mpa.setName(convertMpaNameToCode(name));

        return mpa;
    };

    private String convertMpaNameToCode(String fullName) {
        if (fullName == null) return null;

        switch (fullName.trim()) {
            case "General Audiences":
            case "Для всех возрастов":
                return "G";
            case "Parental Guidance Suggested":
            case "Рекомендуется присутствие родителей":
                return "PG";
            case "Parents Strongly Cautioned":
            case "Родителям настоятельно рекомендуется сопровождение":
                return "PG-13";
            case "Restricted":
            case "До 17 лет обязательно присутствие взрослого":
                return "R";
            case "Adults Only":
            case "Только для взрослых":
                return "NC-17";
            default:
                if (fullName.matches("^(G|PG|PG-13|R|NC-17)$")) {
                    return fullName;
                }
                return fullName; // Оставляем как есть
        }
    }

    @Override
    public Collection<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa_ratings ORDER BY mpa_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("mpa_id"));
            mpa.setCode(rs.getString("code"));
            return mpa;
        });
    }

    @Override
    public Mpa getMpaById(int id) {
        String sql = "SELECT * FROM mpa_ratings WHERE mpa_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Mpa mpa = new Mpa();
                mpa.setId(rs.getInt("mpa_id"));
                mpa.setCode(rs.getString("code"));
                return mpa;
            }, id);
        } catch (Exception e) {
            throw new NotFoundException("MPA с ID " + id + " не найден");
        }
    }
}