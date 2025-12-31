package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashSet;
import java.util.Set;

@Component
public class FilmMapper {

    public Film toEntity(FilmDto dto) {
        Film film = new Film();
        film.setId(dto.getId());
        film.setName(dto.getName());
        film.setDescription(dto.getDescription());
        film.setReleaseDate(dto.getReleaseDate());
        film.setDuration(dto.getDuration());

        if (dto.getMpa() != null && dto.getMpa().getId() != null) {
            film.setMpaId(dto.getMpa().getId());
        }

        if (dto.getGenres() != null && !dto.getGenres().isEmpty()) {
            Set<Integer> genreIds = new HashSet<>();
            for (FilmDto.GenreId genre : dto.getGenres()) {
                if (genre.getId() != null) {
                    genreIds.add(genre.getId());
                }
            }
            film.setGenres(genreIds);
        }

        return film;
    }
}