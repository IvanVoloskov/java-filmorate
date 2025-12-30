package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final MpaStorage mpaStorage;

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        validateFilm(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            throw new ValidationException("ID фильма не может быть null при обновлении");
        }
        validateFilm(film);
        return filmStorage.updateFilm(film);
    }

    public Film getById(int id) {
        return filmStorage.getById(id);
    }

    public void addLike(int userId, int filmId) {
        filmStorage.addLike(filmId, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void removeLike(int userId, int filmId) {
        filmStorage.removeLike(filmId, userId);
        log.info("Пользователь {} убрал лайк фильму {}", userId, filmId);
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .toList();
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание не должно превышать 200 символов");
        }

        if (film.getReleaseDate() == null) {
            throw new ValidationException("Дата выпуска фильма обязательна");
        }

        if (film.getReleaseDate().isBefore(FIRST_FILM_DATE)) {
            throw new ValidationException("Дата выпуска фильма не может быть раньше 28 декабря 1895 года");
        }

        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }

        if (film.getMpaId() == null) {
            throw new ValidationException("Рейтинг MPA не может быть null");
        }

        // Проверка существования MPA в БД
        try {
            mpaStorage.getMpaById(film.getMpaId());
        } catch (NotFoundException e) {
            throw new ValidationException("MPA рейтинг с id = " + film.getMpaId() + " не найден");
        }
    }
}
