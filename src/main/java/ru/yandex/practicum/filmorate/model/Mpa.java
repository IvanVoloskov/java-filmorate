package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Mpa {
    int id;
    @JsonIgnore
    String code;
    @NotBlank
    String name;
}
