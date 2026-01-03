package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class FilmMapper {

    public Film toEntity(FilmDto filmDto) {
        Film film = new Film();
        film.setId(filmDto.getId());
        film.setName(filmDto.getName());
        film.setDescription(filmDto.getDescription());
        film.setReleaseDate(filmDto.getReleaseDate());
        film.setDuration(filmDto.getDuration());

        // Преобразование MPA
        if (filmDto.getMpa() != null && filmDto.getMpa().getId() != null) {
            Mpa mpa = new Mpa();
            mpa.setId(filmDto.getMpa().getId());
            film.setMpa(mpa);
        }

        // Преобразование жанров
        if (filmDto.getGenres() != null && !filmDto.getGenres().isEmpty()) {
            Set<Genre> genres = new LinkedHashSet<>();
            for (FilmDto.GenreDto genreDto : filmDto.getGenres()) {
                Genre genre = new Genre();
                genre.setId(genreDto.getId());
                genres.add(genre);
            }
            film.setGenres(genres);
        }

        return film;
    }

    public FilmDto toDto(Film film) {
        FilmDto filmDto = new FilmDto();
        filmDto.setId(film.getId());
        filmDto.setName(film.getName());
        filmDto.setDescription(film.getDescription());
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setDuration(film.getDuration());

        // MPA
        if (film.getMpa() != null) {
            FilmDto.MpaDto mpaDto = new FilmDto.MpaDto();
            mpaDto.setId(film.getMpa().getId());
            mpaDto.setName(film.getMpa().getName());
            filmDto.setMpa(mpaDto);
        }

        // Жанры
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<FilmDto.GenreDto> genreDtos = new LinkedHashSet<>();
            for (Genre genre : film.getGenres()) {
                FilmDto.GenreDto genreDto = new FilmDto.GenreDto();
                genreDto.setId(genre.getId());
                genreDto.setName(genre.getName());
                genreDtos.add(genreDto);
            }
            filmDto.setGenres(genreDtos);
        }

        return filmDto;
    }
}