package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Optional;

@Repository
@Primary
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper rowMapper;

    public UserDbStorage(JdbcTemplate jdbcTemplate, UserRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }


    public Collection<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public User create(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps; }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);

        if (id != null) {
            user.setId(id);
            return user;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    public User update(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), Date.valueOf(user.getBirthday()), user.getId());
        return user;
    }

    public Optional<User> getUserById(Long id) {
        String sql = "SELECT * FROM users WHERE id =?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, rowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void deleteUser(Long id) {
        jdbcTemplate.update("DELETE FROM users WHERE id =?", id);
    }
}
