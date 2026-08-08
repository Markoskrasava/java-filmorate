package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Override
    public Map<Long, User> getUsers() {
        return users;
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
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

    @Override
    public Optional<User> getUserById(Long id) {
        if (id == null) {
            log.warn("Не введён id пользователя");
            throw new ValidationException("Id должен быть указан");
        }
        if (!users.containsKey(id)) {
            log.warn("Пользователь не найден");
            throw new NotFoundException("Пользователь с указанным Id не найден");
        }
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User addNewFriend(Long id, Long friendId) {
        if (id == null || friendId == null) {
            log.warn("Не введён id одного из пользователей");
            throw new ValidationException("Id должны быть указаны");
        }
        if (!users.containsKey(id)) {
            log.warn("Пользователь не найден");
            throw new NotFoundException("Пользователь с указанным Id не найден");
        }
        if (!users.containsKey(friendId)) {
            log.warn("Пользователь не найден");
            throw new NotFoundException("Пользователь с указанным Id не найден");
        }

        User user = users.get(id);
        User friend = users.get(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
        return user;
    }

    @Override
    public User deleteFriend(Long id, Long friendId) {
        if (id == null || friendId == null) {
            log.warn("Не введён id одного из пользователей");
            throw new ValidationException("Id должны быть указаны");
        }

        if (!users.containsKey(id)) {
            log.warn("Пользователь не найден");
            throw new NotFoundException("Пользователь с указанным Id не найден");
        }
        User user = users.get(id);
        User friend = users.get(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
        return user;
    }

    @Override
    public Collection<User> getAllFriendsByUser(Long id) {
        if (id == null) {
            log.warn("Не введён id пользователя");
            throw new ValidationException("Id пользователя должен быть указан");
        }
        if (!users.containsKey(id)) {
            log.warn("Пользователь не найден");
            throw new NotFoundException("Пользователь с указанным Id не найден");
        }

        User user = users.get(id);
        Set<Long> friendsIds = user.getFriends();
        List<User> friends = new ArrayList<>();
        for (Long friendsid : friendsIds) {
            friends.add(users.get(friendsid));
        }
        return friends;
    }

    @Override
    public Collection<User> getAllGeneralFriends(Long id, Long otherId) {
        if (id == null || otherId == null) {
            log.warn("Не введён id одного из пользователей");
            throw new ValidationException("Id должны быть указаны");
        }
        if (!users.containsKey(id)) {
            log.warn("Пользователь не найден");
            throw new NotFoundException("Пользователь с указанным Id не найден");
        }
        if (!users.containsKey(otherId)) {
            log.warn("Пользователь не найден");
            throw new NotFoundException("Пользователь с указанным Id не найден");
        }

        User user = users.get(id);
        User otherUser = users.get(otherId);
        Set<Long> userFriendsId = user.getFriends();
        Set<Long> otherUserFriendsId = otherUser.getFriends();
        Set<Long> commonIds = userFriendsId.stream()
                .filter(otherUserFriendsId::contains)
                .collect(Collectors.toSet());
        return commonIds.stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public User update(User newUser) {
        if (newUser.getId() == null) {
            log.warn("Не введён id пользователя, которого нужно обновить");
            throw new ValidationException("Id должен быть указан");
        }

        if (!users.containsKey(newUser.getId())) {
            log.warn("Попытка обновления несуществующего пользователя");
            throw new NotFoundException("Пользователь не найден");
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
