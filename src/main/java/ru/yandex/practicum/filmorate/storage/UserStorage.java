package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {

    Collection<User> findAll();

    User create(User user);

    User update(User newUser);

    Optional<User> getUserById(Long id);

    void deleteUser(Long id);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    Collection<User> getFriends(Long userId);

    Set<Long> getFriendIds(Long userId);
}
