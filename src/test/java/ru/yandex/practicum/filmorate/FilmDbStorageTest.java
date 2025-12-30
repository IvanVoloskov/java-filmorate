package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate")
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    @Test
    public void testAddFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        film.setMpaId(1);

        Film addedFilm = filmStorage.addFilm(film);

        assertThat(addedFilm).isNotNull();
        assertThat(addedFilm.getId()).isPositive();
        assertThat(addedFilm.getName()).isEqualTo("Test Film");
    }

    @Test
    public void testGetFilmById() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        film.setMpaId(1);

        Film addedFilm = filmStorage.addFilm(film);
        Film foundFilm = filmStorage.getById(addedFilm.getId());

        assertThat(foundFilm).isNotNull();
        assertThat(foundFilm.getId()).isEqualTo(addedFilm.getId());
    }

    @Test
    public void testGetAllFilms() {
        int initialSize = filmStorage.getAllFilms().size();

        Film film1 = new Film();
        film1.setName("Test Film 1");
        film1.setDescription("Test Description 1");
        film1.setReleaseDate(LocalDate.of(2020, 1, 1));
        film1.setDuration(120);
        film1.setMpaId(1);
        filmStorage.addFilm(film1);

        Film film2 = new Film();
        film2.setName("Test Film 2");
        film2.setDescription("Test Description 2");
        film2.setReleaseDate(LocalDate.of(2021, 5, 15));
        film2.setDuration(90);
        film2.setMpaId(2);
        filmStorage.addFilm(film2);

        Collection<Film> films = filmStorage.getAllFilms();

        assertThat(films).hasSize(initialSize + 2);
    }

    @Test
    public void testUpdateFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        film.setMpaId(1);

        Film addedFilm = filmStorage.addFilm(film);

        addedFilm.setName("Updated Film");
        addedFilm.setDuration(150);

        Film updatedFilm = filmStorage.updateFilm(addedFilm);

        assertThat(updatedFilm.getName()).isEqualTo("Updated Film");
        assertThat(updatedFilm.getDuration()).isEqualTo(150);
    }

    @Test
    public void testAddLike() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        film.setMpaId(1);
        Film addedFilm = filmStorage.addFilm(film);

        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User addedUser = userStorage.addUser(user);

        filmStorage.addLike(addedFilm.getId(), addedUser.getId());

        Collection<Integer> likes = filmStorage.getLikes(addedFilm.getId());

        assertThat(likes).hasSize(1);
        assertThat(likes).contains(addedUser.getId());
    }

    @Test
    public void testRemoveLike() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        film.setMpaId(1);
        Film addedFilm = filmStorage.addFilm(film);

        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User addedUser = userStorage.addUser(user);

        filmStorage.addLike(addedFilm.getId(), addedUser.getId());
        filmStorage.removeLike(addedFilm.getId(), addedUser.getId());

        Collection<Integer> likes = filmStorage.getLikes(addedFilm.getId());

        assertThat(likes).isEmpty();
    }

    @Test
    public void testGetLikes() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        film.setMpaId(1);
        Film addedFilm = filmStorage.addFilm(film);

        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User addedUser = userStorage.addUser(user);

        filmStorage.addLike(addedFilm.getId(), addedUser.getId());

        Collection<Integer> likes = filmStorage.getLikes(addedFilm.getId());

        assertThat(likes).isNotEmpty();
    }
}
