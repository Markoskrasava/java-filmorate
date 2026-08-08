    package ru.yandex.practicum.filmorate.service;

    import org.springframework.stereotype.Service;
    import ru.yandex.practicum.filmorate.model.Film;
    import ru.yandex.practicum.filmorate.storage.FilmStorage;

    import java.util.Collection;
    import java.util.Optional;

    @Service
    public class FilmService {
        private final FilmStorage filmStorage;

        public FilmService(FilmStorage filmStorage) {
            this.filmStorage = filmStorage;
        }

        public Collection<Film> findAll() {
            return filmStorage.findAll();
        }

        public Optional<Film> getFilmById(Long id) {
            return filmStorage.getFilmById(id);
        }

        public Film create(Film film) {
            return filmStorage.create(film);
        }

        public Film update(Film newFilm) {
            return filmStorage.update(newFilm);
        }

        public Film addLike(Long id, Long userId) {
            return filmStorage.addLike(id, userId);
        }

        public Film deleteLike(Long id, Long userId) {
            return filmStorage.deleteLike(id, userId);
        }

        public Collection<Film> getMostLikebleFilms(long count) {
            return filmStorage.getMostLikebleFilms(count);
        }
    }
