package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    int id;
    @Email
    String email;
    @NotBlank
    String login;
    @NotBlank
    String name;
    LocalDate birthday;
}
