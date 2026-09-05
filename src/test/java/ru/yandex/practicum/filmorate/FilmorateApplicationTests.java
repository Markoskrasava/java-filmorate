package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmorateApplicationTests {
    private Film film;
    private FilmController filmController;

    @BeforeEach
    void setUpForFilm() {
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        userController = new UserController(userService);

        InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
        FilmService filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController(filmService);

        film = new Film();
        film.setName("Тестовый фильм");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2024, 1, 1));
        film.setDuration(120);

        user = new User();
        user.setEmail("markbyckov8@gmail.com");
        user.setLogin("MarkosKrasava");
        user.setName("Марк");
        user.setBirthday(LocalDate.of(2003, 11, 25));
    }

	@Test
	void shouldReturnErrorExceptionTextWhenNameIsEmpty() {
        film.setName(" ");
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(film)
        );

        assertEquals("Название не может быть пустым", exception.getMessage());
	}

    @Test
    void shouldReturnErrorExceptionTextWhenDescriptionIsBig() {
        film.setDescription("A".repeat(201));
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(film)
        );

        assertEquals("Описание не может быть больше 200 символов", exception.getMessage());
    }

    @Test
    void shouldReturnErrorExceptionTextWhenDateIsTooAgo() {
        LocalDate localDate = LocalDate.of(1890, 12, 28);
        film.setReleaseDate(localDate);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(film)
        );
        assertEquals("Дата релиза может быть не раньше 28 декабря 1895 года", exception.getMessage());
    }

    @Test
    void shouldReturnErrorExceptionTextWhenDurationIsNegative() {
        film.setDuration(-29);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(film)
        );

        assertEquals("Продолжительность фильма должна быть больше нуля", exception.getMessage());
    }

    @Test
    void shouldReturnErrorExceptionTextWhenIdNotWritten() {
        film.setId(null);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.update(film)
        );

        assertEquals("Id должен быть указан", exception.getMessage());
    }

    @Test
    void shouldReturnErrorExceptionTextWhenTryingUpdateUnknownFilm() {
        Film emptyFilm = new Film();
        emptyFilm.setId(2L);
        emptyFilm.setName("Начало");
        emptyFilm.setDescription("Кристофер Нолан снял фильм о вторжении в сны");
        emptyFilm.setReleaseDate(LocalDate.of(2010, 7, 16));
        emptyFilm.setDuration(148);
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> filmController.update(emptyFilm)
        );

        assertEquals("Фильм не найден", exception.getMessage());
    }

    private User user;
    private UserController userController;

    @Test
    void shouldReturnErrorExceptionTextWhenEmailIsEmpty() {
        user.setEmail(" ");
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.create(user)
        );

        assertEquals("Имейл должен быть указан", exception.getMessage());
    }

    @Test
    void shouldReturnErrorExceptionTextWhenEmailWithoutAt() {
        user.setEmail("markbyckov8gmail.com");
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.create(user)
        );

        assertEquals("В имейле должен содержаться символ @", exception.getMessage());
    }

    @Test
    void shouldReturnErrorExceptionTextWhenLoginIsEmpty() {
        user.setLogin(" ");
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.create(user)
        );

        assertEquals("Логин не может быть пустым", exception.getMessage());
    }

    @Test
    void shouldReturnErrorExceptionTextWhenLoginContainsSpaces() {
        user.setLogin("spa ce");
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.create(user)
        );

        assertEquals("Логин не должен содержать пробелы", exception.getMessage());
    }

    @Test
    void shouldReturnEqualBetweenNameAndLoginIfNameIsEmpty() {
        user.setName(" ");
        userController.create(user);
        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    void shouldReturnErrorExceptionTextWhenBirthdayIsInFuture() {
        user.setBirthday(LocalDate.of(2026, 11, 25));
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.create(user)
        );

        assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
    }

    @Test
    void shouldReturnErrorExceptionTextWhenEmailIsAlreadyUses() {
        User user1 = new User();
        user1.setEmail("duplicate@mail.ru");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user1);

        User user2 = new User();
        user2.setEmail("duplicate@mail.ru"); // Тот же email
        user2.setLogin("user2");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(2000, 1, 1));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.create(user2)
        );

        assertEquals("Этот имейл уже используется", exception.getMessage());
    }

    @Test
    void shouldReturnErrorExceptionTextWhenIdIsNotWritten() {
        user.setId(null);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.update(user)
        );

        assertEquals("Id должен быть указан", exception.getMessage());
    }

    @Test
    void shouldReturnErrorExceptionTextWhenTryingUpdateUnknownUser() {
        User emptyUser = new User();
        emptyUser.setId(2L);
        emptyUser.setEmail("mark@");
        emptyUser.setLogin("Markoolio");
        emptyUser.setName("Марк");
        emptyUser.setBirthday(LocalDate.of(2010, 7, 16));
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userController.update(emptyUser)
        );

        assertEquals("Пользователь не найден", exception.getMessage());
    }
}

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

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, FilmRowMapper.class, MpaRatingRowMapper.class, GenreRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTests {
    private final FilmDbStorage filmStorage;

    @Test
    public void testFindFilmById() {

        Optional<Film> filmOptional = filmStorage.getFilmById(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testCreateFilm() {
        Film film = new Film();
        film.setName("Матрица");
        film.setDescription("Культовый фантастический фильм");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);
        film.setMpaRating(MpaRating.R);

        filmStorage.create(film);
        Optional<Film> userOptional = filmStorage.getFilmById(film.getId());
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(film1 ->
                        assertThat(film1).hasFieldOrPropertyWithValue("name", "Матрица")
                );
    }

    @Test
    public void testFindAll() {
        Collection<Film> films = filmStorage.findAll();
        assertEquals(4, films.size());
    }

    @Test
    public void testUpdateFilm() {
        Film film = new Film();
        film.setName("Матрица");
        film.setDescription("Культовый фантастический фильм");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);
        film.setMpaRating(MpaRating.R);

        filmStorage.create(film);

        film.setName("Матрица-2");
        filmStorage.update(film);

        Optional<Film> filmOptional = filmStorage.getFilmById(film.getId());
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(newFilm ->
                        assertThat(newFilm).hasFieldOrPropertyWithValue("name", "Матрица-2")
                );
    }

    @Test
    public void deleteFilm() {
        MpaRating mpa = MpaRating.fromId(4L);
        Film film = new Film();
        film.setName("Матрица");
        film.setDescription("Культовый фантастический фильм");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);
        film.setMpaRating(mpa);

        filmStorage.create(film);

        Long id = film.getId();
        filmStorage.deleteFilm(id);
        Optional<Film> filmOptional = filmStorage.getFilmById(id);
        assertThat(filmOptional).isEmpty();
    }
}

