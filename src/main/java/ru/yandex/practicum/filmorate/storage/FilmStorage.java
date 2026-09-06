package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film newFilm);

    Optional<Film> getFilmById(Long id);

    void deleteFilm(Long id);

    Collection<MpaRating> getAllMpaRatings();

    Optional<MpaRating> getMpaRatingById(Long id);

    Collection<Genre> getAllGenre();

    Optional<Genre> getGenreById(Long id);

    Collection<Film> getMostPopularFilms(long count);

    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);
}