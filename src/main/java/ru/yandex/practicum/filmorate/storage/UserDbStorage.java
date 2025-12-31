package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.UserUniqueValidator;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Qualifier("userDbStorage")
@Repository
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserValidator userValidator;
    private final UserUniqueValidator userUniqueValidator;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate,
                         UserValidator userValidator,
                         UserUniqueValidator userUniqueValidator) {
        this.jdbcTemplate = jdbcTemplate;
        this.userValidator = userValidator;
        this.userUniqueValidator = userUniqueValidator;
    }

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        Date birthday = rs.getDate("birthday");
        if (birthday != null) {
            user.setBirthday(birthday.toLocalDate());
        }
        return user;
    };

    @Override
    public Collection<User> getAllUsers() {
        String sql = "SELECT * FROM users ORDER BY user_id";
        List<User> users = jdbcTemplate.query(sql, userRowMapper);

        for (User user : users) {
            Set<Integer> friends = getFriends(user.getId());
            user.setFriends(friends);
        }

        return users;
    }

    @Override
    public User addUser(User user) {
        userValidator.validateUser(user);
        userUniqueValidator.validateEmailUnique(user.getEmail(), null);

        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"user_id"});
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getLogin());
                ps.setString(3, user.getName());
                ps.setDate(4, user.getBirthday() != null ? Date.valueOf(user.getBirthday()) : null);
                return ps;
            }, keyHolder);

            user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
            return user;
        } catch (Exception e) {
            // Обработка дубликатов на уровне БД
            if (e.getMessage().toLowerCase().contains("unique") ||
                    e.getMessage().toLowerCase().contains("duplicate")) {
                throw new ValidationException("Email уже используется");
            }
            throw e;
        }
    }

    @Override
    public User updateUser(User user) {
        userValidator.validateForUpdate(user);

        User existingUser = getById(user.getId());
        if (existingUser == null) {
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }

        if (!existingUser.getEmail().equalsIgnoreCase(user.getEmail())) {
            userUniqueValidator.validateEmailUnique(user.getEmail(), user.getId());
        }

        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
                "WHERE user_id = ?";

        int rowsUpdated = jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday() != null ? Date.valueOf(user.getBirthday()) : null,
                user.getId());

        if (rowsUpdated == 0) {
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }

        return getById(user.getId());
    }

    @Override
    public User getById(int id) {
        if (id <= 0) {
            throw new ValidationException("ID пользователя должен быть положительным");
        }

        String sql = "SELECT * FROM users WHERE user_id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, id);

            if (user != null) {
                String friendsSql = "SELECT friend_id FROM friendships WHERE user_id = ?";
                List<Integer> friends = jdbcTemplate.queryForList(friendsSql, Integer.class, id);
                user.setFriends(new HashSet<>(friends));
            }

            return user;
        } catch (NotFoundException e) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении пользователя", e);
        }
    }

    @Override
    public void addFriend(int userId, int friendId) {
        if (userId <= 0 || friendId <= 0) {
            throw new ValidationException("ID пользователя должен быть положительным");
        }

        if (userId == friendId) {
            throw new ValidationException("Пользователь не может добавить сам себя в друзья");
        }

        // Проверяем существование пользователей
        User user = getById(userId);
        User friend = getById(friendId);

        if (user == null || friend == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        // Проверяем, не добавил ли уже в друзья (односторонняя проверка)
        String checkSql = "SELECT COUNT(*) FROM friendships WHERE user_id = ? AND friend_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, userId, friendId);

        if (count > 0) {
            throw new ValidationException("Пользователь уже добавил этого пользователя в друзья");
        }

        // Вставляем только одну запись (односторонняя дружба)
        String sql = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        if (userId <= 0 || friendId <= 0) {
            throw new ValidationException("ID пользователя должен быть положительным");
        }

        if (userId == friendId) {
            throw new ValidationException("Пользователь не может удалить самого себя");
        }

        // Проверяем существование пользователей
        getById(userId);
        getById(friendId);

        // Удаляем только одну запись (односторонняя дружба)
        String sql = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
        int rowsDeleted = jdbcTemplate.update(sql, userId, friendId);

        if (rowsDeleted == 0) {
            throw new NotFoundException("Дружба не найдена (пользователь " + userId +
                    " не добавлял " + friendId + " в друзья)");
        }
    }

    @Override
    public Set<Integer> getFriends(int userId) {
        if (userId <= 0) {
            throw new ValidationException("ID пользователя должен быть положительным");
        }

        // Получаем только друзей, которых пользователь сам добавил
        String sql = "SELECT friend_id FROM friendships WHERE user_id = ?";
        List<Integer> friends = jdbcTemplate.queryForList(sql, Integer.class, userId);
        return new HashSet<>(friends);
    }
}