package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTests {
    private final UserDbStorage userStorage;

    @Test
    public void testFindUserById() {

        Optional<User> userOptional = userStorage.getUserById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setEmail("markbyckov8@gmail.com");
        user.setName("Марк");
        user.setLogin("MarkosKrasava");
        user.setBirthday(LocalDate.of(2003, 11,25));

        userStorage.create(user);
        Optional<User> userOptional = userStorage.getUserById(user.getId());
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user1 ->
                        assertThat(user1).hasFieldOrPropertyWithValue("name", "Марк")
                );
    }

    @Test
    public void testFindAll() {
        Collection<User> users = userStorage.findAll();
        assertEquals(3, users.size());
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setEmail("danilbyckov8@gmail.com");
        user.setName("Данил");
        user.setLogin("DanchikKrasava");
        user.setBirthday(LocalDate.of(2002, 11,25));

        userStorage.create(user);

        user.setName("Линад");
        userStorage.update(user);

        Optional<User> userOptional = userStorage.getUserById(user.getId());
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(userKram ->
                        assertThat(userKram).hasFieldOrPropertyWithValue("name", "Линад")
                );
    }

    @Test
    public void deleteUser() {
        User user = new User();
        user.setEmail("Olegbyckov8@gmail.com");
        user.setName("Олег");
        user.setLogin("OlegKrasava");
        user.setBirthday(LocalDate.of(2003, 11,25));

        userStorage.create(user);
        Long id = user.getId();
        userStorage.deleteUser(id);
        Optional<User> userOptional = userStorage.getUserById(id);
        assertThat(userOptional).isEmpty();
    }
}
