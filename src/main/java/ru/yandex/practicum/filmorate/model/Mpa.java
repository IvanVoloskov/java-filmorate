package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Mpa {
    int id;
    String code;
    @NotBlank
    String name;
}
