package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;

import java.util.Collection;

@Service
public class MpaRatingService {
    private final FilmDbStorage filmDbStorage;

    public MpaRatingService(FilmDbStorage filmDbStorage) {
        this.filmDbStorage = filmDbStorage;
    }

    public Collection<MpaRating> getAllMpaRatings() {
        return filmDbStorage.getAllMpaRatings();
    }

    public MpaRating getMpaRatingById(Long id) {
        return filmDbStorage.getMpaRatingById(id).orElseThrow(() -> new NotFoundException("Рейтинг с указанным Id не найден"));
    }
}
