package ru.yandex.practicum.filmorate.storage;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Repository
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaMapper mpaMapper;

    public MpaDbStorage(JdbcTemplate jdbcTemplate, MpaMapper mpaMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaMapper = mpaMapper;
    }

    private final RowMapper<Mpa> mpaRowMapper = (rs, rowNum) -> {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa_id"));
        mpa.setCode(rs.getString("code"));
        mpa.setName(rs.getString("name"));
        return mpa;
    };

    @Override
    public Mpa getMpaById(int id) {
        String sql = "SELECT mpa_id, code, name FROM mpa_ratings WHERE mpa_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, mpaRowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("MPA с ID " + id + " не найден");
        }
    }

    @Override
    public Collection<Mpa> getAllMpa() {
        String sql = "SELECT mpa_id, code, name FROM mpa_ratings ORDER BY mpa_id";
        return jdbcTemplate.query(sql, mpaRowMapper);
    }

    public MpaDto getMpaDtoById(int id) {
        Mpa mpa = getMpaById(id);
        return mpaMapper.toDto(mpa);
    }

    public Collection<MpaDto> getAllDto() {
        Collection<Mpa> allMpa = getAllMpa();
        return allMpa.stream()
                .map(mpaMapper::toDto)
                .collect(Collectors.toList());
    }
}