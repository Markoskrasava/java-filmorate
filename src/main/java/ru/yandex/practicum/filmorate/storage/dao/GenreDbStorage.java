package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.Collection;
import java.util.Optional;

@Repository
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreRowMapper;

    public GenreDbStorage(JdbcTemplate jdbcTemplate, GenreRowMapper genreRowMapper)  {
        this.jdbcTemplate = jdbcTemplate;
        this.genreRowMapper = genreRowMapper;
    }

    @Override
    public Collection<Genre> getAllGenre() {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql, genreRowMapper);
    }

    @Override
    public Optional<Genre> getGenreById(Long id) {
        String sql = "SELECT * FROM genres WHERE id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, genreRowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
