package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class Film {
    int id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    LocalDate releaseDate;
    int duration;
}
