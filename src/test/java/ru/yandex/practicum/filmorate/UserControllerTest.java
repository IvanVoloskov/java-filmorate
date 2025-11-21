package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setup() {
        userController = new UserController();
    }

    @Test
    void shouldThrowExceptionWhenEmailIsEmpty() {
        User user = new User();
        user.setLogin("user1");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.addUser(user));

        assertEquals("Email пользователя не должен быть пустым и обязательно должен содержать @",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("user1");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.addUser(user));

        assertEquals("Email пользователя не должен быть пустым и обязательно должен содержать @",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenLoginIsEmpty() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.addUser(user));

        assertEquals("Логин пользователя не может быть пустым или содержать пробелы",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenLoginContainsSpaces() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("user name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.addUser(user));

        assertEquals("Логин пользователя не может быть пустым или содержать пробелы",
                exception.getMessage());
    }

    @Test
    void shouldUseLoginAsNameWhenNameIsEmpty() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("user1");
        user.setName("");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User added = userController.addUser(user);
        assertEquals("user1", added.getName());
    }

    @Test
    void shouldThrowExceptionWhenBirthdayInFuture() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("user1");
        user.setBirthday(LocalDate.now().plusDays(1));

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.addUser(user));

        assertEquals("Дата рождения не может быть в будущем!", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        User user = new User();
        user.setId(999);
        user.setEmail("test@example.com");
        user.setLogin("user1");
        user.setBirthday(LocalDate.of(2000,1,1));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userController.updateUser(user));

        assertEquals("Пользователь с таким id не найден", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingEmailToDuplicate() {
        User user1 = new User();
        user1.setEmail("email1@example.com");
        user1.setLogin("user1");
        user1.setBirthday(LocalDate.of(2000,1,1));
        userController.addUser(user1);

        User user2 = new User();
        user2.setEmail("email2@example.com");
        user2.setLogin("user2");
        user2.setBirthday(LocalDate.of(2000,1,1));
        userController.addUser(user2);

        User update = new User();
        update.setId(user2.getId());
        update.setEmail("email1@example.com");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.updateUser(update));

        assertEquals("Этот email уже используется", exception.getMessage());
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("user1");
        user.setName("User One");
        user.setBirthday(LocalDate.of(2000,1,1));

        User added = userController.addUser(user);

        User update = new User();
        update.setId(added.getId());
        update.setEmail("updated@example.com");
        update.setLogin("updatedLogin");
        update.setName("Updated Name");
        update.setBirthday(LocalDate.of(1999,1,1));

        User updated = userController.updateUser(update);

        assertEquals("updated@example.com", updated.getEmail());
        assertEquals("updatedLogin", updated.getLogin());
        assertEquals("Updated Name", updated.getName());
        assertEquals(LocalDate.of(1999,1,1), updated.getBirthday());
    }
}
