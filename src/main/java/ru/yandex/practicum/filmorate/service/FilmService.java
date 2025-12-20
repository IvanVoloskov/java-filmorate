package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getById(int id) {
        return filmStorage.getById(id);
    }

    public void addLike(int idUser, int idFilm) {
        Film film = filmStorage.getById(idFilm);
        User user = userStorage.getById(idUser);
        film.getLikes().add(user.getId());
        log.info("Пользовать {} поставил лайк фильму {}", idUser, idFilm);
    }

    public void removeLike(int idUser, int idFilm) {
        Film film = filmStorage.getById(idFilm);
        User user = userStorage.getById(idUser);
        film.getLikes().remove(user.getId());
        log.info("Пользовать {} убрал лайк фильму {}", idUser, idFilm);
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}

