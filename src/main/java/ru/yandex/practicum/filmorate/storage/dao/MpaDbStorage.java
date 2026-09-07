package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRatingRowMapper;

import java.util.Collection;
import java.util.Optional;

@Repository
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaRatingRowMapper mpaRatingRowMapper;

    public MpaDbStorage(JdbcTemplate jdbcTemplate, MpaRatingRowMapper mpaRatingRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaRatingRowMapper = mpaRatingRowMapper;
    }

    @Override
    public Collection<MpaRating> getAllMpaRatings() {
        String sql = "SELECT * FROM mpa_ratings";
        return jdbcTemplate.query(sql, mpaRatingRowMapper);
    }

    @Override
    public Optional<MpaRating> getMpaRatingById(Long id) {
        String sql = "SELECT * FROM mpa_ratings WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, mpaRatingRowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
