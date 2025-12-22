package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    int id;
    @NotBlank(message = "Название фильма не может быть пустым")
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
    Set<Integer> likes = new HashSet<>();
}
