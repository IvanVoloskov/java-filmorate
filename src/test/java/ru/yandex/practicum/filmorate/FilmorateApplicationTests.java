package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;

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

        // Создаем объект MPA
        FilmDto.MpaId mpaId = new FilmDto.MpaId();
        mpaId.setId(1);
        filmDto.setMpa(mpaId);

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

        FilmDto.MpaId mpaId = new FilmDto.MpaId();
        mpaId.setId(1);
        filmDto.setMpa(mpaId);

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

        FilmDto.MpaId mpaId = new FilmDto.MpaId();
        mpaId.setId(1);
        filmDto.setMpa(mpaId);

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

        FilmDto.MpaId mpaId = new FilmDto.MpaId();
        mpaId.setId(1);
        filmDto.setMpa(mpaId);

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

        FilmDto.MpaId mpaId = new FilmDto.MpaId();
        mpaId.setId(1);
        filmDto.setMpa(mpaId);

        Film addedFilm = filmController.addFilm(filmDto);

        assertNotNull(addedFilm);
        assertEquals("Фильм", addedFilm.getName());
        assertEquals(1, addedFilm.getId());
        assertEquals(1, addedFilm.getMpaId());
    }

    @Test
    void shouldAddValidFilmWithGenresSuccessfully() {
        FilmDto filmDto = new FilmDto();
        filmDto.setName("Фильм с жанрами");
        filmDto.setDescription("Описание фильма");
        filmDto.setReleaseDate(LocalDate.of(2000, 1, 1));
        filmDto.setDuration(120);

        FilmDto.MpaId mpaId = new FilmDto.MpaId();
        mpaId.setId(1);
        filmDto.setMpa(mpaId);

        // Добавляем жанры
        FilmDto.GenreId genre1 = new FilmDto.GenreId();
        genre1.setId(1);
        FilmDto.GenreId genre2 = new FilmDto.GenreId();
        genre2.setId(2);

        filmDto.setGenres(java.util.Set.of(genre1, genre2));

        Film addedFilm = filmController.addFilm(filmDto);

        assertNotNull(addedFilm);
        assertEquals("Фильм с жанрами", addedFilm.getName());
        assertEquals(1, addedFilm.getId());
        assertEquals(1, addedFilm.getMpaId());
        assertNotNull(addedFilm.getGenres());
        assertEquals(2, addedFilm.getGenres().size());
        assertTrue(addedFilm.getGenres().contains(1));
        assertTrue(addedFilm.getGenres().contains(2));
    }

}