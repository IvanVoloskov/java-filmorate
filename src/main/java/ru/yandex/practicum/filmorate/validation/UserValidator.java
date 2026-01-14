package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
@Component
public class UserValidator {

    public void validateUser(User user) {
        validateEmail(user.getEmail());
        validateLogin(user.getLogin());
        validateBirthday(user.getBirthday());
        validateNameIfBlank(user);
    }

    public void validateForUpdate(User user) {
        validateUser(user);
        if (user.getId() <= 0) {
            log.warn("Попытка обновить пользователя с некорректным id {}", user.getId());
            throw new ValidationException("Id не может быть меньше 1");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("Email не может быть пустым");
        }
        if (!email.contains("@")) {
            throw new ValidationException("Email должен содержать @");
        }
    }

    private void validateLogin(String login) {
        if (login == null || login.isBlank()) {
            throw new ValidationException("Логин не может быть пустым");
        }
        if (login.contains(" ")) {
            log.warn("Неверный логин: {}", login);
            throw new ValidationException("Логин пользователя не может содержать пробелы");
        }
    }

    private void validateBirthday(LocalDate birthday) {
        if (birthday == null) {
            throw new ValidationException("Дата рождения не может быть null");
        }
        if (birthday.isAfter(LocalDate.now())) {
            log.warn("Дата рождения в будущем: {}", birthday);
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    private void validateNameIfBlank(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}