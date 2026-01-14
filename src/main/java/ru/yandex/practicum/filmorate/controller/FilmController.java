package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public Collection<FilmDto> getAllFilms() {
        return filmService.getAllFilms();
    }

    @PostMapping
    public FilmDto addFilm(@Valid @RequestBody FilmDto filmDto) {
        return filmService.addFilm(filmDto);
    }

    @PutMapping("/{id}")
    public FilmDto updateFilm(@PathVariable Integer id, @Valid @RequestBody FilmDto filmDto) {
        filmDto.setId(id);
        return filmService.updateFilm(filmDto);
    }

    @PutMapping
    public FilmDto updateFilm(@Valid @RequestBody FilmDto filmDto) {
        return filmService.updateFilm(filmDto);
    }

    @GetMapping("/{id}")
    public FilmDto getFilmById(@PathVariable Integer id) {
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
    public Collection<FilmDto> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getPopularFilms(count);
    }
}