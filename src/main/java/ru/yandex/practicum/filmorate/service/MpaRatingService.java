package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@Service
public class MpaRatingService {
    private final MpaStorage mpaStorage;

    public MpaRatingService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Collection<MpaRating> getAllMpaRatings() {
        return mpaStorage.getAllMpaRatings();
    }

    public MpaRating getMpaRatingById(Long id) {
        return mpaStorage.getMpaRatingById(id).orElseThrow(() -> new NotFoundException("Рейтинг с указанным Id не найден"));
    }
}
