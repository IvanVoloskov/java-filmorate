package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

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
            throw new ValidationException("Пользователь не может добавить сам себя");
        }
        User user = userStorage.getById(idUser);
        User friend = userStorage.getById(idFriend);
        if (user.getFriends().contains(idFriend)) {
            throw new ValidationException("Пользователи уже являются друзьями");
        }
        user.getFriends().add(idFriend);
        friend.getFriends().add(idUser);
        log.info("Пользователь {} добавил в друзья пользователя {}", idUser, idFriend);
    }

    public void removeFriend(int idUser, int idFriend) {
        if (idUser == idFriend) {
            throw new ValidationException("Пользователь не может удалить сам себя");
        }
        User user = userStorage.getById(idUser);
        User friend = userStorage.getById(idFriend);
        user.getFriends().remove((Integer) friend.getId());
        friend.getFriends().remove((Integer) user.getId());
        log.info("Пользователь {} удалил из друзей пользователя {}", idUser, idFriend);
    }

    public Set<Integer> getAllFriends(int idUser) {
        User user = userStorage.getById(idUser);
        return new HashSet<>(user.getFriends());
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
