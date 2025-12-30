package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping("/{id}")
    public Film updateFilm(@PathVariable Integer id, @Valid @RequestBody Film film) {
        film.setId(id);
        return filmService.updateFilm(film);
    }

    @PutMapping("/{idFilm}/like/{idUser}")
    public void addLike(@PathVariable Integer idFilm, @PathVariable Integer idUser) {
        // ИСПРАВЛЕНО: правильный порядок параметров (filmId, userId)
        filmService.addLike(idFilm, idUser);
    }

    @DeleteMapping("/{idFilm}/like/{idUser}")
    public void removeLike(@PathVariable Integer idFilm, @PathVariable Integer idUser) {
        // ИСПРАВЛЕНО: правильный порядок параметров (filmId, userId)
        filmService.removeLike(idFilm, idUser);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getPopularFilms(count);
    }
}