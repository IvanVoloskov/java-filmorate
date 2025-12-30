package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Set;

public interface FilmStorage {

    Collection<Film> getAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getById(int id);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    Set<Integer> getLikes(int filmId);
}
