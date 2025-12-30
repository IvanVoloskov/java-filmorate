package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
public class GenreController {
    private final GenreStorage genreStorage;

    @GetMapping("/genres")
    public Collection<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable int id) {
        return genreStorage.getGenreById(id);
    }
}
