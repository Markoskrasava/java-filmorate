package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Map<Long, Film> getFilms() {
        return films;
    }

    @Override
    public Collection<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        Film temporaryFilm = films.get(newFilm.getId());
        if (temporaryFilm == null) {
            throw new NotFoundException("Фильм не найден");
        }
        temporaryFilm.setDescription(newFilm.getDescription());
        temporaryFilm.setDuration(newFilm.getDuration());
        temporaryFilm.setName(newFilm.getName());
        temporaryFilm.setReleaseDate(newFilm.getReleaseDate());
        return temporaryFilm;
    }

    @Override
    public void deleteFilm(Long id) {
        films.remove(id);
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
