package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User addUser(User user) {
        validateEmailUnique(user.getEmail(), null);
        validateLogin(user.getLogin());
        validateBirthday(user.getBirthday());
        setNameIfBlank(user);

        user.setId(getNextId());
        users.put(user.getId(), user);

        log.info("Пользователь {} успешно добавлен", user.getName());
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        if (newUser.getId() <= 0) {
            log.warn("Попытка обновить пользователя с некорректным id {}", newUser.getId());
            throw new ValidationException("Id не может быть меньше 1");
        }

        User oldUser = users.get(newUser.getId());
        if (oldUser == null) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }

        validateEmailUnique(newUser.getEmail(), newUser.getId());
        validateLogin(newUser.getLogin());
        validateBirthday(newUser.getBirthday());

        // Обновление только тех полей, которые пришли
        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getLogin() != null) {
            oldUser.setLogin(newUser.getLogin());
        }
        if (newUser.getName() != null && !newUser.getName().isBlank()) {
            oldUser.setName(newUser.getName());
        } else {
            setNameIfBlank(oldUser);
        }
        if (newUser.getBirthday() != null) {
            oldUser.setBirthday(newUser.getBirthday());
        }

        log.info("Пользователь {} с id = {} обновлён", oldUser.getName(), oldUser.getId());
        return oldUser;
    }

    @Override
    public User getById(int id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return user;
    }


    private void validateEmailUnique(String email, Integer currentUserId) {
        if (email == null) return;

        boolean exists = users.values().stream()
                .anyMatch(u -> u.getEmail().equals(email)
                        && (currentUserId == null || u.getId() != currentUserId));

        if (exists) {
            log.warn("Попытка использовать уже занятый email: {}", email);
            throw new ValidationException("Этот email уже используется");
        }
    }

    private void validateLogin(String login) {
        if (login == null) return;

        if (login.contains(" ")) {
            log.warn("Неверный логин: {}", login);
            throw new ValidationException("Логин пользователя не может быть пустым или содержать пробелы");
        }
    }

    private void validateBirthday(LocalDate birthday) {
        if (birthday == null) return;

        if (birthday.isAfter(LocalDate.now())) {
            log.warn("Дата рождения в будущем: {}", birthday);
            throw new ValidationException("Дата рождения не может быть в будущем!");
        }
    }

    private void setNameIfBlank(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private int getNextId() {
        return users.keySet().stream().max(Integer::compareTo).orElse(0) + 1;
    }

}
