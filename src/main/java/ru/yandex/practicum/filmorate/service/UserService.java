package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getById(int id) {
        return userStorage.getById(id);
    }

    public void addFriend(int idUser, int idFriend) {
        if (idUser == idFriend) {
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }
        userStorage.addFriend(idUser, idFriend);
        log.info("Пользователь {} добавил в друзья пользователя {}", idUser, idFriend);
    }

    public void removeFriend(int idUser, int idFriend) {
        userStorage.removeFriend(idUser, idFriend);
        log.info("Пользователь {} удалил из друзей пользователя {}", idUser, idFriend);
    }

    public Collection<User> getAllFriends(int idUser) {
        log.info("Получение друзей пользователя {}", idUser);
        Set<Integer> friendIds = userStorage.getFriends(idUser);
        log.info("Найдено {} друзей у пользователя {}", friendIds.size(), idUser);

        Set<User> friends = new HashSet<>();
        for (Integer friendId : friendIds) {
            friends.add(userStorage.getById(friendId));
        }

        return friends;
    }

    public Collection<User> getCommonFriends(int idUser, int otherId) {
        if (idUser == otherId) {
            throw new ValidationException("Нельзя искать общих друзей с самим собой");
        }

        Set<Integer> userFriends = userStorage.getFriends(idUser);
        Set<Integer> otherFriends = userStorage.getFriends(otherId);

        Set<Integer> commonFriendIds = new HashSet<>(userFriends);
        commonFriendIds.retainAll(otherFriends);

        Set<User> commonFriends = new HashSet<>();
        for (Integer friendId : commonFriendIds) {
            commonFriends.add(userStorage.getById(friendId));
        }

        log.info("Найдено {} общих друзей у пользователей {} и {}",
                commonFriends.size(), idUser, otherId);

        return commonFriends;
    }
}