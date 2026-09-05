package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;

import java.util.Collection;

@Service
public class GenreService {
    private final FilmDbStorage filmDbStorage;

    public GenreService(FilmDbStorage filmDbStorage) {
        this.filmDbStorage = filmDbStorage;
    }

    public Collection<Genre> getAllGenre() {
        return filmDbStorage.getAllGenre();
    }

    public Genre getGenreById(Long id) {
        return filmDbStorage.getGenreById(id).orElseThrow(() -> new NotFoundException("Жанр с указанным Id не найден"));
    }
}
