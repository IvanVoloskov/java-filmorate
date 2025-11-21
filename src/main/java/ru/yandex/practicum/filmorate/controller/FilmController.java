package ru.yandex.practicum.filmorate.controller;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAllFilms () {
        return films.values();
    }

    @PostMapping
    public Film addFilm (@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Попытка добавить фильм без названия");
            throw new ValidationException("название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length()>200) {
            log.warn("описание фильм слишком большое: {}", film.getDescription().length());
            throw new ValidationException("Максимальная длина описания фильма - 200 символов");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate
                .of(1895,12,28))) {
            log.warn("Попытка добавить фильм, выпущенный до 1895 года");
            throw new ValidationException("Фильм не должен быть выпущен раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0){
            log.warn("Некорректная продолжительность фильма {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма всегда положительная!");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм {} успешно добавлен", film.getName());
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        if (newFilm.getId() <= 0) {
            log.warn("Попытка обновить фильм с некорректным id {}", newFilm.getId());
            throw new ValidationException("Id фильма должен быть указан");
        }
        Film oldFilm = films.get(newFilm.getId());
        if (oldFilm == null) {
            log.warn("Фильма с id {} нет в коллекции", newFilm.getId());
            throw new ValidationException("Фильм с таким id не найден");
        }
        if (newFilm.getName() != null && !newFilm.getName().isBlank()) {
            oldFilm.setName(newFilm.getName());
        } else if (newFilm.getName() != null) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (newFilm.getDescription() != null) {
            if (newFilm.getDescription().length() > 200) {
                throw new ValidationException("Максимальная длина описания фильма - 200 символов");
            }
            oldFilm.setDescription(newFilm.getDescription());
        }
        if (newFilm.getReleaseDate() != null) {
            if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                throw new ValidationException("Фильм не должен быть выпущен раньше 28 декабря 1895 года");
            }
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }
        if (newFilm.getDuration() > 0) {
            oldFilm.setDuration(newFilm.getDuration());
        } else  {
            throw new ValidationException("Продолжительность фильма всегда положительная!");
        }
        log.info("Обновлён фильм {} с id = {}", oldFilm.getName(), oldFilm.getId());
        return oldFilm;
    }

    private int getNextId() {
        return films.keySet()
                .stream()
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }
}
