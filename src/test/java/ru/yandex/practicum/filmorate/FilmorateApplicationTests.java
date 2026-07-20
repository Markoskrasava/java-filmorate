package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmorateApplicationTests {
    private Film film;
    private FilmController filmController;

    @BeforeEach
    void setUpForFilm() {
        film = new Film();
        filmController = new FilmController();
        film.setName("Тестовый фильм");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2024, 1, 1));
        film.setDuration(120);
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

        assertEquals("Продолжительность фильма не может быть отрицательной", exception.getMessage());
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
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.update(emptyFilm)
        );

        assertEquals("Фильм не найден", exception.getMessage());
    }

    private User user;
    private UserController userController;

    @BeforeEach
    void setUpForUser() {
        user = new User();
        userController = new UserController();
        user.setEmail("markbyckov8@gmail.com");
        user.setLogin("MarkosKrasava");
        user.setName("Марк");
        user.setBirthday(LocalDate.of(2003, 11, 25));
    }

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
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.update(emptyUser)
        );

        assertEquals("Пользователь не найден", exception.getMessage());
    }
}
