package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate")
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    public void testAddUser() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User addedUser = userStorage.addUser(user);

        assertThat(addedUser).isNotNull();
        assertThat(addedUser.getId()).isPositive();
        assertThat(addedUser.getEmail()).isEqualTo("test@mail.com");
    }

    @Test
    public void testGetUserById() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User addedUser = userStorage.addUser(user);
        User foundUser = userStorage.getById(addedUser.getId());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(addedUser.getId());
    }

    @Test
    public void testGetAllUsers() {
        // Получаем начальное количество пользователей
        int initialSize = userStorage.getAllUsers().size();

        User user1 = new User();
        user1.setEmail("test1@mail.com");
        user1.setLogin("testlogin1");
        user1.setName("Test User 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.addUser(user1);

        User user2 = new User();
        user2.setEmail("test2@mail.com");
        user2.setLogin("testlogin2");
        user2.setName("Test User 2");
        user2.setBirthday(LocalDate.of(1995, 5, 15));
        userStorage.addUser(user2);

        Collection<User> users = userStorage.getAllUsers();

        // Проверяем, что добавилось 2 пользователя
        assertThat(users).hasSize(initialSize + 2);
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("testlogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User addedUser = userStorage.addUser(user);

        addedUser.setEmail("updated@mail.com");
        addedUser.setLogin("updatedlogin");

        User updatedUser = userStorage.updateUser(addedUser);

        assertThat(updatedUser.getEmail()).isEqualTo("updated@mail.com");
        assertThat(updatedUser.getLogin()).isEqualTo("updatedlogin");
    }

    @Test
    public void testAddFriend() {
        User user1 = new User();
        user1.setEmail("test1@mail.com");
        user1.setLogin("testlogin1");
        user1.setName("Test User 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        User addedUser1 = userStorage.addUser(user1);

        User user2 = new User();
        user2.setEmail("test2@mail.com");
        user2.setLogin("testlogin2");
        user2.setName("Test User 2");
        user2.setBirthday(LocalDate.of(1995, 5, 15));
        User addedUser2 = userStorage.addUser(user2);

        userStorage.addFriend(addedUser1.getId(), addedUser2.getId());

        Collection<Integer> friends = userStorage.getFriends(addedUser1.getId());

        assertThat(friends).hasSize(1);
        assertThat(friends).contains(addedUser2.getId());
    }

    @Test
    public void testRemoveFriend() {
        User user1 = new User();
        user1.setEmail("test1@mail.com");
        user1.setLogin("testlogin1");
        user1.setName("Test User 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        User addedUser1 = userStorage.addUser(user1);

        User user2 = new User();
        user2.setEmail("test2@mail.com");
        user2.setLogin("testlogin2");
        user2.setName("Test User 2");
        user2.setBirthday(LocalDate.of(1995, 5, 15));
        User addedUser2 = userStorage.addUser(user2);

        userStorage.addFriend(addedUser1.getId(), addedUser2.getId());
        userStorage.removeFriend(addedUser1.getId(), addedUser2.getId());

        Collection<Integer> friends = userStorage.getFriends(addedUser1.getId());

        assertThat(friends).isEmpty();
    }

    @Test
    public void testGetFriends() {
        User user1 = new User();
        user1.setEmail("test1@mail.com");
        user1.setLogin("testlogin1");
        user1.setName("Test User 1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        User addedUser1 = userStorage.addUser(user1);

        User user2 = new User();
        user2.setEmail("test2@mail.com");
        user2.setLogin("testlogin2");
        user2.setName("Test User 2");
        user2.setBirthday(LocalDate.of(1995, 5, 15));
        User addedUser2 = userStorage.addUser(user2);

        userStorage.addFriend(addedUser1.getId(), addedUser2.getId());

        Collection<Integer> friends = userStorage.getFriends(addedUser1.getId());

        assertThat(friends).isNotEmpty();
    }
}