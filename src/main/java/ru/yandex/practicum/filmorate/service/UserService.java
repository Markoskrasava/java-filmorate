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

    public void addNewFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            throw new ValidationException("Id должны быть указаны");
        }
        getUserById(userId);
        getUserById(friendId);
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            throw new ValidationException("Id должны быть указаны");
        }
        Set<Long> userFriends = userStorage.getFriendIds(userId);
        if (!userFriends.contains(friendId)) {
            throw new NotFoundException("Друг с id " + friendId + " не найден у пользователя " + userId);
        }
        userStorage.deleteFriend(userId, friendId);
    }

    public Collection<User> getAllFriendsByUser(Long id) {
        if (id == null) {
            log.warn("Не введён id пользователя");
            throw new ValidationException("Id пользователя должен быть указан");
        }

        getUserById(id);
        return userStorage.getFriends(id);
    }

    public Collection<User> getAllGeneralFriends(Long id, Long otherId) {
        getUserById(id);
        getUserById(otherId);

        Set<Long> userFriendIds = userStorage.getFriendIds(id);
        Set<Long> otherFriendIds = userStorage.getFriendIds(otherId);

        Set<Long> commonIds = new HashSet<>(userFriendIds);
        commonIds.retainAll(otherFriendIds);

        return commonIds.stream()
                .map(userId -> userStorage.getUserById(userId)
                        .orElseThrow(() -> new NotFoundException("Друг с id " + userId + " не найден")))
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

        User existingUser = userStorage.getUserById(newUser.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + newUser.getId() + " не найден"));

        if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
            log.warn("Попытка обновления пользователя с пустым email");
            throw new ValidationException("Имейл должен быть указан");
        } else if (!newUser.getEmail().contains("@")) {
            log.warn("Попытка обновления пользователя с email без символа @");
            throw new ValidationException("В имейле должен содержаться символ @");
        }

        if (newUser.getLogin() == null || newUser.getLogin().isBlank()) {
            log.warn("Попытка обновления пользователя с пустым login");
            throw new ValidationException("Логин не может быть пустым");
        } else if (newUser.getLogin().contains(" ")) {
            log.warn("Попытка обновления login с пробелами");
            throw new ValidationException("Логин не должен содержать пробелы");
        }

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            existingUser.setName(newUser.getLogin());
            log.debug("name пустой, поэтому ему присваивается значение login'а");
        } else {
            existingUser.setName(newUser.getName());
        }

        if (newUser.getBirthday() == null) {
            log.warn("Попытка с пустой датой");
            throw new ValidationException("Дата не может быть пустой");
        }
        if (newUser.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Попытка указать birthday из будущего");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        existingUser.setEmail(newUser.getEmail());
        existingUser.setLogin(newUser.getLogin());
        existingUser.setBirthday(newUser.getBirthday());

        return userStorage.update(existingUser);
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
