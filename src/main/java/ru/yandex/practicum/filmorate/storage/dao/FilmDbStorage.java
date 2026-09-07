package ru.yandex.practicum.filmorate.storage.dao;

import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper rowMapper;
    private static final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);

    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public Film create(Film film) {
        if (film.getReleaseDate() == null) {
            throw new ValidationException("Дата релиза должна быть указана");
        }

        if (film.getGenre() != null && !film.getGenre().isEmpty()) {
            List<Genre> distinct = film.getGenre().stream()
                    .distinct()
                    .collect(Collectors.toList());
            film.setGenre(distinct);
        }

        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, film.getName());
                ps.setString(2, film.getDescription());
                ps.setDate(3, Date.valueOf(film.getReleaseDate()));
                ps.setInt(4, film.getDuration());
                ps.setLong(5, film.getMpaRating().getId());
                return ps;
            }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        if (id == null) {
            throw new InternalServerException("Не удалось сохранить фильм");
        }
        film.setId(id);

        if (film.getGenre() != null && !film.getGenre().isEmpty()) {
            String genreSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(genreSql, film.getGenre(), film.getGenre().size(),
                    (ps, genre) -> {
                        ps.setLong(1, id);
                        ps.setLong(2, genre.getId());
                    });
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpaRating().getId(),
                film.getId());

        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        if (film.getGenre() != null && !film.getGenre().isEmpty()) {
            List<Genre> distinct = film.getGenre().stream()
                    .distinct()
                    .collect(Collectors.toList());
            film.setGenre(distinct);
            String genreSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(genreSql, film.getGenre(), film.getGenre().size(),
                    (ps, genre) -> {
                        ps.setLong(1, film.getId());
                        ps.setLong(2, genre.getId());
                    });
        }
        return film;
    }

    @Override
    public void deleteFilm(Long id) {
        jdbcTemplate.update("DELETE FROM films WHERE id = ?", id);
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id, " +
                "m.name AS mpa_name," +
                "               g.id AS genre_id, g.name AS genre_name," +
                "               l.user_id AS like_user_id" +
                "        FROM films f" +
                "        LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id" +
                "        LEFT JOIN film_genres fg ON f.id = fg.film_id" +
                "        LEFT JOIN genres g ON fg.genre_id = g.id" +
                "        LEFT JOIN likes l ON f.id = l.film_id" +
                "        ORDER BY f.id";
        return findFilmsWithDetails(sql);
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id, " +
                "m.name AS mpa_name, " +
                "               g.id AS genre_id, g.name AS genre_name," +
                "               l.user_id AS like_user_id" +
                "        FROM films f" +
                "        LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id" +
                "        LEFT JOIN film_genres fg ON f.id = fg.film_id" +
                "        LEFT JOIN genres g ON fg.genre_id = g.id" +
                "        LEFT JOIN likes l ON f.id = l.film_id" +
                "        WHERE f.id = ?" +
                "        ORDER BY f.id";
        Collection<Film> films = findFilmsWithDetails(sql, id);
        if (films.isEmpty()) {
            return Optional.empty();
        }
        return films.stream().findFirst();
    }

    @Override
    public Collection<Film> getMostPopularFilms(long count) {
        String sql = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id," +
        "m.name AS mpa_name, " +
                "g.id AS genre_id, g.name AS genre_name, " +
                "l.user_id AS like_user_id " +
        "FROM films f " +
        "LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id " +
        "LEFT JOIN film_genres fg ON f.id = fg.film_id " +
        "LEFT JOIN genres g ON fg.genre_id = g.id " +
        "LEFT JOIN likes l ON f.id = l.film_id " +
        "GROUP BY f.id, m.name, g.id, g.name, l.user_id " +
        "ORDER BY COUNT(DISTINCT l.user_id) DESC, f.id ASC " +
        "OFFSET 0 ROWS " +
                "FETCH NEXT ? ROWS ONLY";
        return findFilmsWithDetails(sql, count);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "MERGE INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    private Collection<Film> findFilmsWithDetails(String sql, Object... args) {
        Map<Long, Film> filmMap = new LinkedHashMap<>();

        jdbcTemplate.query(sql, rs -> {
            Long filmId = rs.getLong("id");

            Film film = filmMap.get(filmId);
            if (film == null) {
                film = rowMapper.mapRow(rs, rs.getRow());
                film.setGenre(new ArrayList<>());
                film.setLikes(new HashSet<>());
                filmMap.put(filmId, film);
            }

            Long genreId = rs.getLong("genre_id");
            if (!rs.wasNull()) {
                boolean exists = film.getGenre().stream()
                        .anyMatch(g -> g.getId().equals(genreId));
                if (!exists) {
                    Genre genre = new Genre();
                    genre.setId(genreId);
                    genre.setName(rs.getString("genre_name"));
                    film.getGenre().add(genre);
                }
            }

            Long likeUserId = rs.getLong("like_user_id");
            if (!rs.wasNull()) {
                film.getLikes().add(likeUserId);
            }
        }, args);

        return filmMap.values();
    }
}
