package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Slf4j
@Repository
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Film> filmRowMapper = (rs, rowNum) -> {
        Film film = new Film();
        film.setId(rs.getInt("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setDuration(rs.getInt("duration"));
        Date releaseDate = rs.getDate("release_date");
        if (releaseDate != null) {
            film.setReleaseDate(releaseDate.toLocalDate());
        }
        film.setMpaId(rs.getInt("mpa_id"));
        return film;
    };

    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT * FROM films";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);

        for (Film film : films) {
            Set<Integer> likes = getLikes(film.getId());
            Set<Integer> genres = getFilmGenres(film.getId());
            film.setLikes(likes);
            film.setGenres(genres);
        }

        return films;
    }

    @Override
    public Film addFilm(Film film) {
        String sql = """
            INSERT INTO films (name, description, release_date, duration, mpa_id)
            VALUES (?, ?, ?, ?, ?)
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpaId());
            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = """
            UPDATE films
            SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
            WHERE film_id = ?
            """;

        int rowsAffected = jdbcTemplate.update(
                sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpaId(),
                film.getId()
        );

        if (rowsAffected == 0) {
            throw new NotFoundException("Фильм с ID " + film.getId() + " не найден");
        }

        // Перезагружаем фильм с обновлёнными likes и genres
        return getById(film.getId());
    }

    @Override
    public Film getById(int id) {
        try {
            String sql = "SELECT * FROM films WHERE film_id = ?";
            Film film = jdbcTemplate.queryForObject(sql, filmRowMapper, id);

            if (film != null) {
                Set<Integer> likes = getLikes(id);
                Set<Integer> genres = getFilmGenres(id);
                film.setLikes(likes);
                film.setGenres(genres);
            }

            return film;
        } catch (Exception e) {
            throw new NotFoundException("Фильм с ID " + id + " не найден");
        }
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sql, filmId, userId);
        } catch (Exception e) {
            // Возможно, дублирующая запись — игнорируем
            log.warn("Пользователь {} уже поставил лайк фильму {}", userId, filmId);
        }
    }

    @Override
    public void removeLike(int filmId, int userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, filmId, userId);

        if (rowsAffected == 0) {
            log.warn("Лайк пользователя {} для фильма {} не найден", userId, filmId);
        }
    }

    @Override
    public Set<Integer> getLikes(int filmId) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        try {
            return new HashSet<>(jdbcTemplate.queryForList(sql, Integer.class, filmId));
        } catch (Exception e) {
            log.error("Ошибка при получении лайков для фильма ID {}: {}", filmId, e.getMessage());
            return new HashSet<>();
        }
    }

    private Set<Integer> getFilmGenres(int filmId) {
        String sql = "SELECT genre_id FROM film_genres WHERE film_id = ?";
        try {
            return new HashSet<>(jdbcTemplate.queryForList(sql, Integer.class, filmId));
        } catch (Exception e) {
            log.error("Ошибка при получении жанров для фильма ID {}: {}", filmId, e.getMessage());
            return new HashSet<>();
        }
    }
}
