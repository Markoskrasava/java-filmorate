    package ru.yandex.practicum.filmorate.service;

    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.stereotype.Service;
    import ru.yandex.practicum.filmorate.controller.FilmController;
    import ru.yandex.practicum.filmorate.exception.NotFoundException;
    import ru.yandex.practicum.filmorate.exception.ValidationException;
    import ru.yandex.practicum.filmorate.model.Film;
    import ru.yandex.practicum.filmorate.model.Genre;
    import ru.yandex.practicum.filmorate.model.MpaRating;
    import ru.yandex.practicum.filmorate.storage.FilmStorage;
    import ru.yandex.practicum.filmorate.storage.UserStorage;

    import java.time.LocalDate;
    import java.util.*;
    import java.util.stream.Collectors;

    @Service
    public class FilmService {
        private final FilmStorage filmStorage;
        private final UserStorage userStorage;
        public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
        private static final Logger log = LoggerFactory.getLogger(FilmController.class);

        public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
            this.filmStorage = filmStorage;
            this.userStorage = userStorage;
        }

        public Collection<Film> findAll() {
            return filmStorage.findAll();
        }

        public Film getFilmById(Long id) {
            if (id == null) {
                log.warn("Не введён id фильма");
                throw new ValidationException("Id должен быть указан");
            }

            return filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("Фильм с указанным Id не найден"));
        }

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

            if (film.getMpaRating() == null || film.getMpaRating().getId() == null) {
                throw new ValidationException("Рейтинг MPA должен быть указан");
            }
            MpaRating mpa = filmStorage.getMpaRatingById(film.getMpaRating().getId())
                    .orElseThrow(() -> new NotFoundException("Рейтинг MPA с id " + film.getMpaRating().getId() + " не найден"));
            film.setMpaRating(mpa);

            if (film.getGenre() != null && !film.getGenre().isEmpty()) {
                List<Genre> validGenres = new ArrayList<>();
                for (Genre genre : film.getGenre()) {
                    Genre found = filmStorage.getGenreById(genre.getId())
                            .orElseThrow(() -> new NotFoundException("Жанр с id " + genre.getId() + " не найден"));
                    validGenres.add(found);
                }
                film.setGenre(validGenres);
            }
            return filmStorage.create(film);
        }

        public Film update(Film newFilm) {
            if (newFilm.getId() == null) {
                log.warn("Не введён id фильма, которого нужно обновить");
                throw new ValidationException("Id должен быть указан");
            }

            filmStorage.getFilmById(newFilm.getId())
                    .orElseThrow(() -> new NotFoundException("Фильм с id " + newFilm.getId() + " не найден"));

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
            return filmStorage.update(newFilm);
        }

        public void addLike(Long id, Long userId) {
            if (id == null) {
                log.warn("Не введён id фильма");
                throw new ValidationException("Id фильма должен быть указан");
            }
            if (userId == null) {
                log.warn("Не введён id пользователя");
                throw new ValidationException("Id пользователя должен быть указан");
            }
            Film film = getFilmById(id);
            userStorage.getUserById(userId)
                    .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
            film.getLikes().add(userId);
            filmStorage.update(film);
        }

        public void deleteLike(Long id, Long userId) {
            if (id == null) {
                log.warn("Не введён id фильма");
                throw new ValidationException("Id фильма должен быть указан");
            }
            if (userId == null) {
                log.warn("Не введён id пользователя");
                throw new ValidationException("Id пользователя должен быть указан");
            }
            Film film = getFilmById(id);
            userStorage.getUserById(userId)
                    .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
            film.getLikes().remove(userId);
            filmStorage.update(film);
        }

        public Collection<Film> getMostPopularFilms(long count) {
            List<Film> sorted = filmStorage.findAll().stream()
                    .sorted((f1, f2) -> {
                        int cmp = Integer.compare(f2.getLikes().size(), f1.getLikes().size());
                        if (cmp == 0) {
                            return Long.compare(f1.getId(), f2.getId());
                        }
                        return cmp;
                    })
                    .limit(count)
                    .collect(Collectors.toList());
            return sorted;
        }

        public void deleteFilm(Long id) {
            if (id == null) {
                log.warn("Не введён id фильма");
                throw new ValidationException("Id фильма должен быть указан");
            }
            try {
                filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("Фильм с указанным id не найден"));
            } catch (NotFoundException e) {
                log.warn("Фильм не найден");
            }
            filmStorage.deleteFilm(id);
        }
    }
