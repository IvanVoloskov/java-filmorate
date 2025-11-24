package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class Film {
    int id;
    @NotBlank
    @NonNull
    String name;
    @NonNull
    @NotBlank
    String description;
    LocalDate releaseDate;
    int duration;
}
