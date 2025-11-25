package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController controller;

    @BeforeEach
    void setup() {
        controller = new UserController();
    }

    @Test
    void shouldAddUserSuccessfully() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("userLogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User added = controller.addUser(user);

        assertEquals(1, added.getId());
        assertEquals("User Name", added.getName());
    }

    @Test
    void shouldUseLoginIfNameIsEmpty() {
        User user = new User();
        user.setEmail("user2@example.com");
        user.setLogin("userLogin2");
        user.setName(""); // пустое имя
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User added = controller.addUser(user);

        assertEquals("userLogin2", added.getName());
    }

    @Test
    void shouldThrowWhenLoginContainsSpaces() {
        User user = new User();
        user.setEmail("user3@example.com");
        user.setLogin("user login"); // пробелы в логине
        user.setName("User Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        assertThrows(ValidationException.class, () -> controller.addUser(user));
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        User user1 = new User();
        user1.setEmail("user4@example.com");
        user1.setLogin("user4");
        user1.setName("User 4");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        controller.addUser(user1);

        User user2 = new User();
        user2.setEmail("user4@example.com");
        user2.setLogin("user5");
        user2.setName("User 5");
        user2.setBirthday(LocalDate.of(1991, 1, 1));

        assertThrows(ValidationException.class, () -> controller.addUser(user2));
    }

    @Test
    void shouldThrowWhenBirthdayInFuture() {
        User user = new User();
        user.setEmail("user6@example.com");
        user.setLogin("user6");
        user.setName("User 6");
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> controller.addUser(user));
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        User user = new User();
        user.setEmail("user7@example.com");
        user.setLogin("user7");
        user.setName("User 7");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User added = controller.addUser(user);

        User update = new User();
        update.setId(added.getId());
        update.setEmail("user7@example.com");
        update.setLogin("updatedLogin");
        update.setName("Updated Name");
        update.setBirthday(LocalDate.of(1991, 2, 2));

        User updated = controller.updateUser(update);

        assertEquals("updatedLogin", updated.getLogin());
        assertEquals("Updated Name", updated.getName());
        assertEquals(LocalDate.of(1991, 2, 2), updated.getBirthday());
    }
}
