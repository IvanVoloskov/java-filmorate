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
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

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
        Integer mpaId = rs.getInt("mpa_id");
        if (!rs.wasNull() && mpaId != null) {
            Mpa mpa = new Mpa();
            mpa.setId(mpaId);
            film.setMpa(mpa);
        }
        return film;
    };

    // RowMapper для Genre
    private final RowMapper<Genre> genreRowMapper = (rs, rowNum) -> {
        Genre genre = new Genre();
        genre.setId(rs.getInt("genre_id"));
        genre.setName(rs.getString("name"));
        return genre;
    };

    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT * FROM films";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);

        for (Film film : films) {
            Set<Integer> likes = getLikes(film.getId());
            Set<Genre> genres = getFilmGenres(film.getId());
            film.setLikes(likes);
            film.setGenres(genres);

            if (film.getMpa() != null) {
                Mpa fullMpa = getMpaById(film.getMpa().getId());
                film.setMpa(fullMpa);
            }
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
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        saveFilmGenres(film.getId(), film.getGenres());

        return getById(film.getId());
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
                film.getMpa().getId(),
                film.getId()
        );

        if (rowsAffected == 0) {
            throw new NotFoundException("Фильм с ID " + film.getId() + " не найден");
        }

        updateFilmGenres(film.getId(), film.getGenres());

        return getById(film.getId());
    }

    @Override
    public Film getById(int id) {
        try {
            String sql = "SELECT * FROM films WHERE film_id = ?";
            Film film = jdbcTemplate.queryForObject(sql, filmRowMapper, id);

            if (film != null) {
                Set<Integer> likes = getLikes(id);
                Set<Genre> genres = getFilmGenres(id);
                film.setLikes(likes);
                film.setGenres(genres);

                if (film.getMpa() != null) {
                    Mpa fullMpa = getMpaById(film.getMpa().getId());
                    film.setMpa(fullMpa);
                }
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

    private Set<Genre> getFilmGenres(int filmId) {
        String sql = "SELECT g.genre_id, g.name " +
                "FROM genres g " +
                "JOIN film_genres fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ? " +
                "ORDER BY g.genre_id";
        try {
            List<Genre> genresList = jdbcTemplate.query(sql, genreRowMapper, filmId);
            return new LinkedHashSet<>(genresList);
        } catch (Exception e) {
            log.error("Ошибка при получении жанров для фильма ID {}: {}", filmId, e.getMessage());
            return new LinkedHashSet<>();
        }
    }

    // Метод для получения полной информации о MPA (с названием)
    private Mpa getMpaById(int mpaId) {
        String sql = "SELECT * FROM mpa_ratings WHERE mpa_id = ?";
        try {
            RowMapper<Mpa> mpaRowMapper = (rs, rowNum) -> {
                Mpa mpa = new Mpa();
                mpa.setId(rs.getInt("mpa_id"));
                mpa.setName(rs.getString("name"));
                mpa.setName(convertMpaNameToCode(mpa.getName()));
                return mpa;
            };
            return jdbcTemplate.queryForObject(sql, mpaRowMapper, mpaId);
        } catch (Exception e) {
            log.error("MPA с ID {} не найден", mpaId);
            return null;
        }
    }

    private void saveFilmGenres(int filmId, Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }

        String deleteSql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(deleteSql, filmId);

        // Добавляем новые жанры
        String insertSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : genres) {
            jdbcTemplate.update(insertSql, filmId, genre.getId());
        }
    }

    private void updateFilmGenres(int filmId, Set<Genre> genres) {
        saveFilmGenres(filmId, genres);
    }

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
                return fullName;
        }
    }
}