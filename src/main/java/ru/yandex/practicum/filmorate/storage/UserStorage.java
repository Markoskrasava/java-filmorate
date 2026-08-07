package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface UserStorage {

    Collection<User> findAll();

    User create(User user);

    User update(User newUser);

    Optional<User> getUserById(Long id);

    User addNewFriend(Long id, Long friendId);

    User deleteFriend(Long id, Long friendId);

    Collection<User> getAllFriendsByUser(Long id);

    Collection<User> getAllGeneralFriends(Long id, Long otherId);

    Map<Long, User> getUsers();
    
}
