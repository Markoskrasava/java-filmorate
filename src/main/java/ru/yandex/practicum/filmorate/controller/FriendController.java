package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("users")
public class FriendController {
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {}

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {}

    @GetMapping("/{id}/friends")
    public List<Object> getFriends(@PathVariable Long id) {
        return Collections.emptyList();
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<Object> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return Collections.emptyList();
    }
}
