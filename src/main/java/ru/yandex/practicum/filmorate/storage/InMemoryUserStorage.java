package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User update(User newUser) {
        User temporaryUser = users.get(newUser.getId());
        if (temporaryUser == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        temporaryUser.setName(newUser.getName());
        temporaryUser.setLogin(newUser.getLogin());
        temporaryUser.setEmail(newUser.getEmail());
        temporaryUser.setBirthday(newUser.getBirthday());
        return temporaryUser;
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(id);
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
