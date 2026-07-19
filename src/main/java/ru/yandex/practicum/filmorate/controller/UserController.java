package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private static final  Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Попытка создания пользователя с пустым email");
            throw new ValidationException("Имейл должен быть указан");
        }

        if (!user.getEmail().contains("@")) {
            log.warn("Попытка создания пользователя с email без символа @");
            throw new ValidationException("В имейле должен содержаться символ @");
        }

        if (user.getLogin().isBlank()) {
            log.warn("Попытка создания пользователя с пустым login");
            throw new ValidationException("Логин не может быть пустым");
        }

        if (user.getLogin().trim().contains(" ")) {
            log.warn("Попытка создания login с пробелами");
            throw new ValidationException("Логин не должен содержать пробелы");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("name пустой, поэтому ему присваивается значение login'а");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Попытка указать birthday из будущего");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        for (Map.Entry<Long, User> entry : users.entrySet()) {
            String userEmail = entry.getValue().getEmail();
            if (userEmail.equals(user.getEmail())) {
                log.warn("Попытка создания пользователя с email, который уже зарегестрирован");
                throw new ValidationException("Этот имейл уже используется");
            }
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.warn("Не введён id пользователя, которого нужно обновить");
            throw new ValidationException("Id должен быть указан");
        }

        if (!users.containsKey(newUser.getId())) {
            log.warn("Попытка обновления несуществующего пользователя");
            throw new ValidationException("Пользователь не найден");
        }

        if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
            log.warn("Попытка создания пользователя с пустым email");
            throw new ValidationException("Имейл должен быть указан");
        } else if (!newUser.getEmail().contains("@")) {
            log.warn("Попытка создания пользователя с email без символа @");
            throw new ValidationException("В имейле должен содержаться символ @");
        }

        if (newUser.getLogin().isBlank()) {
            log.warn("Попытка создания пользователя с пустым login");
            throw new ValidationException("Логин не может быть пустым");
        } else if (newUser.getLogin().trim().contains(" ")) {
            log.warn("Попытка создания login с пробелами");
            throw new ValidationException("Логин не должен содержать пробелы");
        }

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
            log.debug("name пустой, поэтому ему присваивается значение login'а");
        }

        if (newUser.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Попытка указать birthday из будущего");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        users.put(newUser.getId(), newUser);
        return newUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
