package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {
    Collection<User> getAllUsers();

    User addUser(User user);

    User updateUser(User user);

    User getById(int id);

    void addFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    Set<Integer> getFriends(int userId);
}
