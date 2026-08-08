package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final UserStorage userStorage;

    public InMemoryFilmStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film create(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Попытка указать пустой name");
            throw new ValidationException("Название не может быть пустым");
        }

        if (film.getDescription() == null) {
            log.warn("Попытка указать пустой description");
            throw new ValidationException("Описание не может быть пустым");
        }

        if (film.getDescription().length() > 200) {
            log.warn("В description больше 200 символов");
            throw new ValidationException("Описание не может быть больше 200 символов");
        }

        if (film.getReleaseDate() == null) {
            log.warn("Пустая дата релиза");
            throw new ValidationException("Дата релиза должна быть указана");
        }

        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Попытка указать releaseDate до 28.12.1895");
            throw new ValidationException("Дата релиза может быть не раньше 28 декабря 1895 года");
        }

        if (film.getDuration() == null) {
            log.warn("Попытка указать пустой duration");
            throw new ValidationException("Продолжительность должна быть указана");
        }

        if (film.getDuration() <= 0) {
            log.warn("Попытка указать duration отрицательным, либо ноль");
            throw new ValidationException("Продолжительность фильма должна быть больше нуля");
        }

        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("Не введён id фильма, которого нужно обновить");
            throw new ValidationException("Id должен быть указан");
        }

        if (!films.containsKey(newFilm.getId())) {
            log.warn("Попытка обновления несуществующего пользователя");
            throw new NotFoundException("Пользователь не найден");
        }

        if (newFilm.getName() == null || newFilm.getName().isBlank()) {
            log.warn("Попытка указать пустой name");
            throw new ValidationException("Название не может быть пустым");
        }

        if (newFilm.getDescription() == null) {
            log.warn("Попытка указать пустой description");
            throw new ValidationException("Описание не может быть пустым");
        }

        if (newFilm.getDescription().length() > 200) {
            log.warn("В description больше 200 символов");
            throw new ValidationException("Описание не может быть больше 200 символов");
        }

        if (newFilm.getReleaseDate() == null) {
            log.warn("Пустая дата релиза");
            throw new ValidationException("Дата релиза должна быть указана");
        }

        if (newFilm.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Попытка указать releaseDate до 28.12.1895");
            throw new ValidationException("Дата релиза может быть не раньше 28 декабря 1895 года");
        }

        if (newFilm.getDuration() == null) {
            log.warn("Попытка указать пустой duration");
            throw new ValidationException("Продолжительность должна быть указана");
        }

        if (newFilm.getDuration() <= 0) {
            log.warn("Попытка указать duration отрицательным, либо ноль");
            throw new ValidationException("Продолжительность фильма должна быть больше нуля");
        }

        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public Film addLike(Long id, Long userId) {
        if (id == null) {
            log.warn("Не введён id фильма");
            throw new ValidationException("Id фильма должен быть указан");
        }
        if (userId == null) {
            log.warn("Не введён id пользователя");
            throw new ValidationException("Id пользователя должен быть указан");
        }
        if (!films.containsKey(id)) {
            log.warn("Фильм не найден");
            throw new NotFoundException("Фильм с указанным Id не найден");
        }
        if (!userStorage.getUsers().containsKey(userId)) {
            log.warn("Пользователь не найден");
            throw new NotFoundException("Пользователь с указанным Id не найден");
        }
        Film film = films.get(id);
        film.getLikes().add(userId);
        return film;
    }

    @Override
    public Film deleteLike(Long id, Long userId) {
        if (id == null) {
            log.warn("Не введён id фильма");
            throw new ValidationException("Id фильма должен быть указан");
        }
        if (userId == null) {
            log.warn("Не введён id пользователя");
            throw new ValidationException("Id пользователя должен быть указан");
        }
        if (!films.containsKey(id)) {
            log.warn("Фильм не найден");
            throw new NotFoundException("Фильм с указанным Id не найден");
        }
        if (!userStorage.getUsers().containsKey(userId)) {
            log.warn("Пользователь не найден");
            throw new NotFoundException("Пользователь с указанным Id не найден");
        }
        Film film = films.get(id);
        film.getLikes().remove(userId);
        return film;
    }

    @Override
    public Collection<Film> getMostLikebleFilms(long count) {
        List<Film> sorted = films.values().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
        return sorted;
    }
}
