package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User getUserById(Long id) {
        if (id == null) {
            log.warn("Не введён id пользователя");
            throw new ValidationException("Id должен быть указан");
        }

        return userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь с указанным Id не найден"));
    }

    public void addNewFriend(Long id, Long friendId) {
        if (id == null || friendId == null) {
            log.warn("Не введён id одного из пользователей");
            throw new ValidationException("Id должны быть указаны");
        }

        User user = getUserById(id);
        User friend = getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
        userStorage.update(user);
        userStorage.update(friend);
    }

    public void deleteFriend(Long id, Long friendId) {
        if (id == null || friendId == null) {
            log.warn("Не введён id одного из пользователей");
            throw new ValidationException("Id должны быть указаны");
        }
        User user = getUserById(id);
        User friend = getUserById(friendId);
        if (user == null) {
            log.warn("Пользователь не найден, выброшен RuntimeEx" +
                    "ception");
            throw new RuntimeException("Пользователь с указанным Id не найден");
        }
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
        userStorage.update(user);
        userStorage.update(friend);
    }

    public Collection<User> getAllFriendsByUser(Long id) {
        if (id == null) {
            log.warn("Не введён id пользователя");
            throw new ValidationException("Id пользователя должен быть указан");
        }

        User user = getUserById(id);
        Set<Long> friendsIds = user.getFriends();
        List<User> friends = new ArrayList<>();
        for (Long friendsId : friendsIds) {
            friends.add(getUserById(friendsId));
        }
        return friends;
    }

    public Collection<User> getAllGeneralFriends(Long id, Long otherId) {
        if (id == null || otherId == null) {
            log.warn("Не введён id одного из пользователей");
            throw new ValidationException("Id должны быть указаны");
        }
        try {
            userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь с указанным id не найден"));
        } catch (NotFoundException e) {
            log.warn("Пользователь не найден");
        }
        try {
            userStorage.getUserById(otherId).orElseThrow(() -> new NotFoundException("Пользователь с указанным id не найден"));
        } catch (NotFoundException e) {
            log.warn("Пользователь не найден");
        }

        User user = getUserById(id);
        User otherUser = getUserById(otherId);
        Set<Long> userFriendsId = user.getFriends();
        Set<Long> otherUserFriendsId = otherUser.getFriends();
        return userFriendsId.stream()
                .filter(otherUserFriendsId::contains)
                .map(friendId -> userStorage.getUserById(friendId)
                        .orElseThrow(() -> new NotFoundException("Друг с id " + friendId + " не найден")))
                .collect(Collectors.toList());
    }

    public User create(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Попытка создания пользователя с пустым email");
            throw new ValidationException("Имейл должен быть указан");
        }

        if (!user.getEmail().contains("@")) {
            log.warn("Попытка создания пользователя с email без символа @");
            throw new ValidationException("В имейле должен содержаться символ @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Попытка создания пользователя с пустым login");
            throw new ValidationException("Логин не может быть пустым");
        }

        if (user.getLogin().contains(" ")) {
            log.warn("Попытка создания login с пробелами");
            throw new ValidationException("Логин не должен содержать пробелы");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("name пустой, поэтому ему присваивается значение login'а");
        }

        if (user.getBirthday() == null) {
            log.warn("Попытка с пустой датой");
            throw new ValidationException("Дата не может быть пустой");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Попытка указать birthday из будущего");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        for (User entry : userStorage.findAll()) {
            String userEmail = entry.getEmail();
            if (userEmail.equals(user.getEmail())) {
                log.warn("Попытка создания пользователя с email, который уже зарегестрирован");
                throw new ValidationException("Этот имейл уже используется");
            }
        }
        return userStorage.create(user);
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            log.warn("Не введён id пользователя, которого нужно обновить");
            throw new ValidationException("Id должен быть указан");
        }

        if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
            log.warn("Попытка создания пользователя с пустым email");
            throw new ValidationException("Имейл должен быть указан");
        } else if (!newUser.getEmail().contains("@")) {
            log.warn("Попытка создания пользователя с email без символа @");
            throw new ValidationException("В имейле должен содержаться символ @");
        }

        if (newUser.getLogin() == null || newUser.getLogin().isBlank()) {
            log.warn("Попытка создания пользователя с пустым login");
            throw new ValidationException("Логин не может быть пустым");
        } else if (newUser.getLogin().contains(" ")) {
            log.warn("Попытка создания login с пробелами");
            throw new ValidationException("Логин не должен содержать пробелы");
        }

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
            log.debug("name пустой, поэтому ему присваивается значение login'а");
        }

        if (newUser.getBirthday() == null) {
            log.warn("Попытка с пустой датой");
            throw new ValidationException("Дата не может быть пустой");
        }

        if (newUser.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Попытка указать birthday из будущего");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        return userStorage.update(newUser);
    }

    public void deleteUser(Long id) {
        if (id == null) {
            log.warn("Не введён id фильма");
            throw new ValidationException("Id пользователя должен быть указан");
        }
        try {
            userStorage.getUserById(id).orElseThrow(() -> new NotFoundException("Пользователь с указанным id не найден"));
        } catch (NotFoundException e) {
            log.warn("Пользователь не найден");
        }
        userStorage.deleteUser(id);
    }
}
