package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class User {
    int id;
    @Email(message = "Not valid email format")
    @NotBlank
    String email;
    @NotBlank
    String login;
    String name;
    LocalDate birthday;
    Set<Integer> friends;
}
