package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        if (film.getName().isBlank()) {
            log.warn("Попытка указать пустой name");
            throw new ValidationException("Название не может быть пустым");
        }

        if (film.getDescription().length() > 200) {
            log.warn("В description больше 200 символов");
            throw new ValidationException("Описание не может быть больше 200 символов");
        }

        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Попытка указать releaseDate до 28.12.1895");
            throw new ValidationException("Дата релиза может быть не раньше 28 декабря 1895 года");
        }

        if (film.getDuration() < 0) {
            log.warn("Попытка указать duration меньше нуля");
            throw new ValidationException("Продолжительность фильма не может быть отрицательной");
        }

        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("Не введён id фильма, которого нужно обновить");
            throw new ValidationException("Id должен быть указан");
        }

        if (!films.containsKey(newFilm.getId())) {
            log.warn("Попытка обновления несуществующего фильма");
            throw new ValidationException("Фильм не найден");
        }

        if (newFilm.getName().isBlank()) {
            log.warn("Попытка указать пустой name");
            throw new ValidationException("Название не может быть пустым");
        }

        if (newFilm.getDescription().length() > 200) {
            log.warn("В description больше 200 символов");
            throw new ValidationException("Описание не может быть больше 200 символов");
        }

        if (newFilm.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Попытка указать releaseDate до 28.12.1895");
            throw new ValidationException("Дата релиза может быть не раньше 28 декабря 1895 года");
        }

        if (newFilm.getDuration() < 0) {
            log.warn("Попытка указать duration меньше нуля");
            throw new ValidationException("Продолжительность фильма не может быть отрицательной");
        }

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
}
