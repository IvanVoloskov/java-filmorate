package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Попытка добавить пользователя без корректного email адреса");
            throw new ValidationException("Email пользователя не должен быть пустым и обязательно должен содержать @");
        }

        for (User u : users.values()) {
            if (user.getEmail().equals(u.getEmail())) {
                log.warn("Попытка добавить пользователя, email которого уже используется");
                throw new ValidationException("Этот email уже используется");
            }
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Попытка добавить пользователя с некорректным логином");
            throw new ValidationException("Логин пользователя не может быть пустым или содержать пробелы");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Попытка добавить пользователя без имени");
            user.setName(user.getLogin());
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Попытка добавить пользователя, у которого указана некорректная дата рождения");
            throw new ValidationException("Дата рождения не может быть в будущем!");
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("пользователь {} успешно добавлен", user.getName());
        return user;
    }


    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        if (newUser.getId() <= 0) {
            log.warn("Попытка обновить пользователя с некорректным id {}", newUser.getId());
            throw new ValidationException("Id не может быть меньше 1");
        }
        User oldUser = users.get(newUser.getId());
        if (oldUser == null) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        if (newUser.getEmail() != null) {
            if (newUser.getEmail().isBlank() || !newUser.getEmail().contains("@")) {
                throw new ValidationException("Email пользователя не должен быть пустым " +
                        "и обязательно должен содержать @");
            }
            for (User u : users.values()) {
                if (u.getId() != (newUser.getId()) && newUser.getEmail().equals(u.getEmail())) {
                    throw new ValidationException("Этот email уже используется");
                }
            }
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getLogin() != null) {
            if (newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
                throw new ValidationException("Логин пользователя не может быть пустым или содержать пробелы");
            }
            oldUser.setLogin(newUser.getLogin());
        }
        if (newUser.getName() != null && !newUser.getName().isBlank()) {
            oldUser.setName(newUser.getName());
        } else if (oldUser.getName() == null || oldUser.getName().isBlank()) {
            oldUser.setName(oldUser.getLogin());
        }
        if (newUser.getBirthday() != null) {
            if (newUser.getBirthday().isAfter(LocalDate.now())) {
                throw new ValidationException("Дата рождения не может быть в будущем!");
            }
            oldUser.setBirthday(newUser.getBirthday());
        }
        log.info("Пользователя {} с id = {} обновлён", oldUser.getName(), oldUser.getId());
        return oldUser;
    }


    private int getNextId() {
        return users.keySet()
                .stream()
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }
}
