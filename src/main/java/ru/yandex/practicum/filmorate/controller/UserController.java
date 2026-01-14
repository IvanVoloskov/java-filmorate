package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

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

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Integer id, @Valid @RequestBody User user) {
        user.setId(id);
        return userService.updateUser(user);
    }

    @GetMapping("/{idUser}/friends")
    public Collection<User> getAllFriends(@PathVariable Integer idUser) {
        userService.getById(idUser);
        return userService.getAllFriends(idUser);
    }

    @GetMapping("/{idUser}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable Integer idUser, @PathVariable Integer otherId) {
        return userService.getCommonFriends(idUser, otherId);
    }

    @PutMapping("/{idUser}/friends/{idFriend}")
    public void addFriend(@PathVariable Integer idUser, @PathVariable Integer idFriend) {
        userService.addFriend(idUser, idFriend);
    }

    @DeleteMapping("/{idUser}/friends/{idFriend}")
    public void removeFriend(@PathVariable Integer idUser, @PathVariable Integer idFriend) {
        userService.removeFriend(idUser, idFriend);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        return userService.getById(id);
    }
}