package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmorateApplicationTests {

    private FilmController filmController;
    private FilmMapper filmMapper;

    @BeforeEach
    void setup() {
        InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
        FilmService filmService = new FilmService(filmStorage);
        filmMapper = new FilmMapper();
        filmController = new FilmController(filmService, filmMapper);
    }

    @Test
    void shouldThrowExceptionWhenNameIsEmpty() {
        FilmDto filmDto = new FilmDto();
        filmDto.setDescription("Описание");
        filmDto.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmDto.setDuration(120);

        // Создаем объект MPA - ИСПРАВЛЕНО: MpaDto вместо MpaId
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

        // ИСПРАВЛЕНО: MpaDto вместо MpaId
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

        // ИСПРАВЛЕНО: MpaDto вместо MpaId
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

        // ИСПРАВЛЕНО: MpaDto вместо MpaId
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

        // ИСПРАВЛЕНО: MpaDto вместо MpaId
        FilmDto.MpaDto mpaDto = new FilmDto.MpaDto();
        mpaDto.setId(1);
        filmDto.setMpa(mpaDto);

        Film addedFilm = filmController.addFilm(filmDto);

        assertNotNull(addedFilm);
        assertEquals("Фильм", addedFilm.getName());
        assertEquals(1, addedFilm.getId());
        // ИСПРАВЛЕНО: Теперь получаем MPA объект и проверяем его ID
        assertNotNull(addedFilm.getMpa());
        assertEquals(1, addedFilm.getMpa().getId());
    }

    @Test
    void shouldAddValidFilmWithGenresSuccessfully() {
        FilmDto filmDto = new FilmDto();
        filmDto.setName("Фильм с жанрами");
        filmDto.setDescription("Описание фильма");
        filmDto.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmDto.setDuration(120);

        // ИСПРАВЛЕНО: MpaDto вместо MpaId
        FilmDto.MpaDto mpaDto = new FilmDto.MpaDto();
        mpaDto.setId(1);
        filmDto.setMpa(mpaDto);

        // ИСПРАВЛЕНО: GenreDto вместо GenreId
        Set<FilmDto.GenreDto> genres = new HashSet<>();
        FilmDto.GenreDto genre1 = new FilmDto.GenreDto();
        genre1.setId(1);
        FilmDto.GenreDto genre2 = new FilmDto.GenreDto();
        genre2.setId(2);
        genres.add(genre1);
        genres.add(genre2);
        filmDto.setGenres(genres);

        Film addedFilm = filmController.addFilm(filmDto);

        assertNotNull(addedFilm);
        assertEquals("Фильм с жанрами", addedFilm.getName());
        assertEquals(1, addedFilm.getId());
        assertNotNull(addedFilm.getMpa());
        assertEquals(1, addedFilm.getMpa().getId());
        assertNotNull(addedFilm.getGenres());
        assertEquals(2, addedFilm.getGenres().size());

        // ИСПРАВЛЕНО: Теперь жанры это Set<Genre>, проверяем по ID
        boolean hasGenre1 = addedFilm.getGenres().stream().anyMatch(g -> g.getId() == 1);
        boolean hasGenre2 = addedFilm.getGenres().stream().anyMatch(g -> g.getId() == 2);
        assertTrue(hasGenre1);
        assertTrue(hasGenre2);
    }
}