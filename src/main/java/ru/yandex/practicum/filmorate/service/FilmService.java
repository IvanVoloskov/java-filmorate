package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private final FilmMapper filmMapper;

    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    public Collection<FilmDto> getAllFilms() {
        Collection<Film> films = filmStorage.getAllFilms();
        Collection<FilmDto> filmDtos = new java.util.ArrayList<>();
        for (Film film : films) {
            filmDtos.add(filmMapper.toDto(film));
        }
        return filmDtos;
    }

    public FilmDto addFilm(FilmDto filmDto) {
        Film film = filmMapper.toEntity(filmDto);
        validateFilm(film);
        validateMpaAndGenres(film);
        Film savedFilm = filmStorage.addFilm(film);
        return filmMapper.toDto(savedFilm);
    }

    public FilmDto updateFilm(FilmDto filmDto) {
        if (filmDto.getId() == null) {
            throw new ValidationException("ID фильма не может быть null");
        }

        filmStorage.getById(filmDto.getId());

        Film film = filmMapper.toEntity(filmDto);
        validateFilm(film);
        validateMpaAndGenres(film);
        Film updatedFilm = filmStorage.updateFilm(film);
        return filmMapper.toDto(updatedFilm);
    }

    public FilmDto getById(int id) {
        Film film = filmStorage.getById(id);
        return filmMapper.toDto(film);
    }

    public void addLike(int filmId, int userId) {
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        filmStorage.removeLike(filmId, userId);
    }

    public Collection<FilmDto> getPopularFilms(int count) {
        Collection<Film> films = filmStorage.getAllFilms();

        java.util.List<Film> filmList = new java.util.ArrayList<>(films);
        filmList.sort((f1, f2) -> {
            int likes1 = f1.getLikes() != null ? f1.getLikes().size() : 0;
            int likes2 = f2.getLikes() != null ? f2.getLikes().size() : 0;
            return Integer.compare(likes2, likes1);
        });

        Collection<FilmDto> popularFilms = new java.util.ArrayList<>();
        int taken = 0;
        for (Film film : filmList) {
            if (taken >= count) break;
            popularFilms.add(filmMapper.toDto(film));
            taken++;
        }
        return popularFilms;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание не должно превышать 200 символов");
        }

        if (film.getReleaseDate() == null) {
            throw new ValidationException("Дата релиза не может быть null");
        }

        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }

    private void validateMpaAndGenres(Film film) {
        if (film.getMpa() == null) {
            throw new ValidationException("MPA рейтинг обязателен");
        }

        Mpa existingMpa = mpaStorage.getMpaById(film.getMpa().getId());
        if (existingMpa == null) {
            throw new NotFoundException("MPA рейтинг с ID " + film.getMpa().getId() + " не найден");
        }

        film.setMpa(existingMpa);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Genre> validGenres = new LinkedHashSet<>();
            for (Genre genre : film.getGenres()) {
                if (genre.getId() == null) {
                    throw new ValidationException("ID жанра не может быть null");
                }

                Genre existingGenre = genreStorage.getGenreById(genre.getId());
                if (existingGenre == null) {
                    throw new NotFoundException("Жанр с ID " + genre.getId() + " не найден");
                }
                validGenres.add(existingGenre);
            }
            film.setGenres(validGenres);
        }
    }
}