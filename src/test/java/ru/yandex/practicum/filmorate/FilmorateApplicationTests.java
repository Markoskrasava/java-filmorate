package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

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
        try {
            filmController.create(film);
        } catch (ValidationException exception) {
            Assertions.assertEquals("Название не может быть пустым", exception.getMessage());
        }
	}

    @Test
    void shouldReturnErrorExceptionTextWhenDescriptionIsBig() {
        film.setDescription("A".repeat(201));
        try {
            filmController.create(film);
        } catch (ValidationException exception) {
            Assertions.assertEquals("Описание не может быть больше 200 символов", exception.getMessage());
        }
    }

    @Test
    void shouldReturnErrorExceptionTextWhenDateIsTooAgo() {
        LocalDate localDate = LocalDate.of(1890, 12, 28);
        film.setReleaseDate(localDate);
        try {
            filmController.create(film);
        } catch (ValidationException exception) {
            Assertions.assertEquals("Дата релиза может быть не раньше 28 декабря 1895 года", exception.getMessage());
        }
    }

    @Test
    void shouldReturnErrorExceptionTextWhenDurationIsNegative() {
        film.setDuration(-29);
        try {
            filmController.create(film);
        } catch (ValidationException exception) {
            Assertions.assertEquals("Продолжительность фильма не может быть отрицательной", exception.getMessage());
        }
    }

    @Test
    void shouldReturnErrorExceptionTextWhenIdNotWritten() {
        film.setId(null);
        try {
            filmController.update(film);
        } catch (ValidationException exception) {
            Assertions.assertEquals("Id должен быть указан", exception.getMessage());
        }
    }

    @Test
    void shouldReturnErrorExceptionTextWhenTryingUpdateUnknownFilm() {
        Film emptyFilm = new Film();
        emptyFilm.setId(2L);
        emptyFilm.setName("Начало");
        emptyFilm.setDescription("Кристофер Нолан снял фильм о вторжении в сны");
        emptyFilm.setReleaseDate(LocalDate.of(2010, 7, 16));
        emptyFilm.setDuration(148);
        try {
            filmController.update(emptyFilm);
        } catch (ValidationException exception) {
            Assertions.assertEquals("Фильм не найден", exception.getMessage());
        }
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
        try {
            userController.create(user);
        } catch (ValidationException exception) {
            Assertions.assertEquals("Имейл должен быть указан", exception.getMessage());
        }
    }

    @Test
    void shouldReturnErrorExceptionTextWhenEmailWithoutAt() {
        user.setEmail("markbyckov8gmail.com");
        try {
            userController.create(user);
        } catch (ValidationException exception) {
            Assertions.assertEquals("В имейле должен содержаться символ @", exception.getMessage());
        }
    }

    @Test
    void shouldReturnErrorExceptionTextWhenLoginIsEmpty() {
        user.setLogin(" ");
        try {
            userController.create(user);
        } catch (ValidationException exception) {
            Assertions.assertEquals("Логин не может быть пустым", exception.getMessage());
        }
    }

    @Test
    void shouldReturnErrorExceptionTextWhenLoginContainsSpaces() {
        user.setLogin("spa ce");
        try {
            userController.create(user);
        } catch (ValidationException exception) {
            Assertions.assertEquals("Логин не должен содержать пробелы", exception.getMessage());
        }
    }

    @Test
    void shouldReturnEqualBetweenNameAndLoginIfNameIsEmpty() {
        user.setName(" ");
        userController.create(user);
        Assertions.assertEquals(user.getLogin(), user.getName());
    }

    @Test
    void shouldReturnErrorExceptionTextWhenBirthdayIsInFuture() {
        user.setBirthday(LocalDate.of(2026, 11, 25));
        try {
            userController.create(user);
        } catch (ValidationException exception) {
            Assertions.assertEquals("Дата рождения не может быть в будущем", exception.getMessage());
        }
    }

    @Test
    void shouldReturnErrorExceptionTextWhenEmailIsAlreadyUses() {
        User user1 = user;
        try {
            userController.create(user);
            userController.create(user1);
        } catch (ValidationException exception) {
            Assertions.assertEquals("Этот имейл уже используется", exception.getMessage());
        }
    }

    @Test
    void shouldReturnErrorExceptionTextWhenIdIsNotWritten() {
        user.setId(null);
        try {
            userController.update(user);
        } catch (ValidationException exception) {
            Assertions.assertEquals("Id должен быть указан", exception.getMessage());
        }
    }

    @Test
    void shouldReturnErrorExceptionTextWhenTryingUpdateUnknownUser() {
        User emptyUser = new User();
        emptyUser.setId(2L);
        emptyUser.setEmail("mark@");
        emptyUser.setLogin("Markoolio");
        emptyUser.setName("Марк");
        emptyUser.setBirthday(LocalDate.of(2010, 7, 16));
        try {
            userController.update(emptyUser);
        } catch (ValidationException exception) {
            Assertions.assertEquals("Пользователь не найден", exception.getMessage());
        }
    }
}
