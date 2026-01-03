package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmorateApplicationTests {

    private FilmController filmController;

    @BeforeEach
    void setup() {
        FilmStorage filmStorage = new SimpleFilmStorage();
        MpaStorage mpaStorage = new SimpleMpaStorage();
        GenreStorage genreStorage = new SimpleGenreStorage();
        FilmMapper filmMapper = new FilmMapper();

        FilmService filmService = new FilmService(filmStorage, mpaStorage, genreStorage, filmMapper);
        filmController = new FilmController(filmService);
    }

    static class SimpleFilmStorage implements FilmStorage {
        @Override
        public Collection<Film> getAllFilms() {
            return new HashSet<>();
        }

        @Override
        public Film addFilm(Film film) {
            film.setId(1);
            return film;
        }

        @Override
        public Film updateFilm(Film film) {
            return film;
        }

        @Override
        public Film getById(int id) {
            Film film = new Film();
            film.setId(id);
            return film;
        }

        @Override
        public void addLike(int filmId, int userId) {
        }

        @Override
        public void removeLike(int filmId, int userId) {
        }

        @Override
        public Set<Integer> getLikes(int filmId) {
            return new HashSet<>();
        }
    }

    static class SimpleMpaStorage implements MpaStorage {
        @Override
        public Mpa getMpaById(int id) {
            if (id == 1) {
                Mpa mpa = new Mpa();
                mpa.setId(1);
                mpa.setName("G");
                return mpa;
            }
            return null;
        }

        @Override
        public Collection<Mpa> getAllMpa() {
            return new HashSet<>();
        }
    }

    static class SimpleGenreStorage implements GenreStorage {
        @Override
        public Genre getGenreById(int id) {
            if (id == 1 || id == 2) {
                Genre genre = new Genre();
                genre.setId(id);
                genre.setName(id == 1 ? "Комедия" : "Драма");
                return genre;
            }
            return null;
        }

        @Override
        public Collection<Genre> getAllGenres() {
            return new HashSet<>();
        }
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        FilmDto filmDto = new FilmDto();
        filmDto.setDescription("Описание");
        filmDto.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmDto.setDuration(120);

        FilmDto.MpaDto mpaDto = new FilmDto.MpaDto();
        mpaDto.setId(1);
        filmDto.setMpa(mpaDto);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.addFilm(filmDto));
        assertEquals("Название фильма не может быть пустым", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDescriptionTooLong() {
        FilmDto filmDto = new FilmDto();
        filmDto.setName("Фильм");
        filmDto.setDescription("A".repeat(201));
        filmDto.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmDto.setDuration(120);

        FilmDto.MpaDto mpaDto = new FilmDto.MpaDto();
        mpaDto.setId(1);
        filmDto.setMpa(mpaDto);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.addFilm(filmDto));
        assertEquals("Описание не должно превышать 200 символов", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenReleaseDateBefore1895() {
        FilmDto filmDto = new FilmDto();
        filmDto.setName("Фильм");
        filmDto.setDescription("Описание");
        filmDto.setReleaseDate(LocalDate.of(1800, 1, 1));
        filmDto.setDuration(120);

        FilmDto.MpaDto mpaDto = new FilmDto.MpaDto();
        mpaDto.setId(1);
        filmDto.setMpa(mpaDto);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.addFilm(filmDto));
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDurationNotPositive() {
        FilmDto filmDto = new FilmDto();
        filmDto.setName("Фильм");
        filmDto.setDescription("Описание");
        filmDto.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmDto.setDuration(0);

        FilmDto.MpaDto mpaDto = new FilmDto.MpaDto();
        mpaDto.setId(1);
        filmDto.setMpa(mpaDto);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.addFilm(filmDto));
        assertEquals("Продолжительность фильма должна быть положительной", exception.getMessage());
    }

    @Test
    void shouldAddValidFilmSuccessfully() {
        FilmDto filmDto = new FilmDto();
        filmDto.setName("Фильм");
        filmDto.setDescription("Описание");
        filmDto.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmDto.setDuration(120);

        FilmDto.MpaDto mpaDto = new FilmDto.MpaDto();
        mpaDto.setId(1);
        filmDto.setMpa(mpaDto);

        FilmDto addedFilmDto = filmController.addFilm(filmDto);

        assertNotNull(addedFilmDto);
        assertEquals("Фильм", addedFilmDto.getName());
        assertEquals(Integer.valueOf(1), addedFilmDto.getId());
        assertNotNull(addedFilmDto.getMpa());
        assertEquals(Integer.valueOf(1), addedFilmDto.getMpa().getId());
    }

    @Test
    void shouldAddValidFilmWithGenresSuccessfully() {
        FilmDto filmDto = new FilmDto();
        filmDto.setName("Фильм с жанрами");
        filmDto.setDescription("Описание фильма");
        filmDto.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmDto.setDuration(120);

        FilmDto.MpaDto mpaDto = new FilmDto.MpaDto();
        mpaDto.setId(1);
        filmDto.setMpa(mpaDto);

        Set<FilmDto.GenreDto> genres = new HashSet<>();
        FilmDto.GenreDto genre1 = new FilmDto.GenreDto();
        genre1.setId(1);
        FilmDto.GenreDto genre2 = new FilmDto.GenreDto();
        genre2.setId(2);
        genres.add(genre1);
        genres.add(genre2);
        filmDto.setGenres(genres);

        FilmDto addedFilmDto = filmController.addFilm(filmDto);

        assertNotNull(addedFilmDto);
        assertEquals("Фильм с жанрами", addedFilmDto.getName());
        assertEquals(Integer.valueOf(1), addedFilmDto.getId());
        assertNotNull(addedFilmDto.getMpa());
        assertEquals(Integer.valueOf(1), addedFilmDto.getMpa().getId());
        assertNotNull(addedFilmDto.getGenres());
        assertEquals(2, addedFilmDto.getGenres().size());

        boolean hasGenre1 = addedFilmDto.getGenres().stream().anyMatch(g -> g.getId() == 1);
        boolean hasGenre2 = addedFilmDto.getGenres().stream().anyMatch(g -> g.getId() == 2);
        assertTrue(hasGenre1);
        assertTrue(hasGenre2);
    }
}