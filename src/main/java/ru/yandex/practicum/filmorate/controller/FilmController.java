package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;
    private final FilmMapper filmMapper;

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody FilmDto filmDto) {
        Film film = filmMapper.toEntity(filmDto);
        return filmService.addFilm(film);
    }

    @PutMapping("/{id}")
    public Film updateFilm(@PathVariable Integer id, @Valid @RequestBody FilmDto filmDto) {
        filmDto.setId(id);
        Film film = filmMapper.toEntity(filmDto);
        return filmService.updateFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody FilmDto filmDto) {
        Film film = filmMapper.toEntity(filmDto);
        return filmService.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        return filmService.getById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Integer filmId,
                        @PathVariable("userId") Integer userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") Integer filmId,
                           @PathVariable("userId") Integer userId) {
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getPopularFilms(count);
    }
}