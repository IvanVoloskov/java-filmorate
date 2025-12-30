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
        userStorage.addFriend(idUser, idFriend);
        log.info("Пользователь {} добавил в друзья пользователя {}", idUser, idFriend);
    }

    public void removeFriend(int idUser, int idFriend) {
        userStorage.removeFriend(idUser, idFriend);
        log.info("Пользователь {} удалил из друзей пользователя {}", idUser, idFriend);
    }

    public Set<Integer> getAllFriends(int idUser) {
        return userStorage.getFriends(idUser);
    }

    public Collection<User> getCommonFriends(int idUser, int otherId) {
        if (idUser == otherId) {
            throw new ValidationException("Нельзя искать общих друзей с самим собой");
        }

        User user = userStorage.getById(idUser);
        User otherUser = userStorage.getById(otherId);

        Set<Integer> commonFriendIds = new HashSet<>(user.getFriends());
        commonFriendIds.retainAll(otherUser.getFriends()); // пересечения двух множеств

        Set<User> commonFriends = new HashSet<>();
        for (Integer friendId : commonFriendIds) {
            commonFriends.add(userStorage.getById(friendId));
        }

        log.info("Найдено {} общих друзей у пользователей {} и {}",
                commonFriends.size(), idUser, otherId);

        return commonFriends;
    }


}
