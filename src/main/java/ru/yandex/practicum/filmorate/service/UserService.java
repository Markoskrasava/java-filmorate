package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Service
public class UserService {
    private UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public User addNewFriend(Long id, Long friendId) {
        return userStorage.addNewFriend(id, friendId);
    }

    public User deleteFriend(Long id, Long friendId) {
        return userStorage.deleteFriend(id, friendId);
    }

    public Collection<User> getAllFriendsByUser(Long id) {
        return userStorage.getAllFriendsByUser(id);
    }

    public Collection<User> getAllGeneralFriends(Long id, Long otherId) {
        return userStorage.getAllGeneralFriends(id, otherId);
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User newUser) {
        return userStorage.update(newUser);
    }
}
