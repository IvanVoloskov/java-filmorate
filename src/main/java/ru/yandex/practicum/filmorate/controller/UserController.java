package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping("/{idUser}/friends")
    public Collection<User> getAllFriends(@PathVariable int idUser) {
        Set<Integer> friendIds = userService.getAllFriends(idUser);

        // Преобразуем ID в объекты User
        List<User> friends = new ArrayList<>();
        for (Integer friendId : friendIds) {
            friends.add(userService.getById(friendId));
        }

        return friends;
    }

    @GetMapping("/{idUser}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable int idUser, @PathVariable int otherId) {
        return userService.getCommonFriends(idUser, otherId);
    }

    @PutMapping("/{idUser}/friends/{idFriend}")
    public void addFriend(@PathVariable int idUser, @PathVariable int idFriend) {
        userService.addFriend(idUser, idFriend);
    }

    @DeleteMapping("/{idUser}/friends/{idFriend}")
    public void removeFriend(@PathVariable int idUser, @PathVariable int idFriend) {
        userService.removeFriend(idUser, idFriend);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getById(id);
    }

}
