package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Qualifier("userMemoryStorage")
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

        oldUser.setEmail(newUser.getEmail());
        oldUser.setLogin(newUser.getLogin());
        oldUser.setName(newUser.getName());
        oldUser.setBirthday(newUser.getBirthday());

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

    @Override
    public void addFriend(int userId, int friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);

        if (user == null || friend == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователь {} добавил в друзья {} (in-memory)", userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);

        if (user == null || friend == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователь {} удалил из друзей {} (in-memory)", userId, friendId);
    }

    @Override
    public Set<Integer> getFriends(int userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        return user.getFriends();
    }

    private void validateEmailUnique(String email, Integer currentUserId) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("Email не может быть пустым");
        }

        boolean exists = users.values().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email.trim())
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
