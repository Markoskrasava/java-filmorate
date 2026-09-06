package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRatingRowMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, FilmRowMapper.class, MpaRatingRowMapper.class, GenreRowMapper.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTests {
    private final FilmDbStorage filmStorage;

    @Test
    public void testFindFilmById() {

        Optional<Film> filmOptional = filmStorage.getFilmById(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testCreateFilm() {
        Film film = new Film();
        film.setName("Матрица");
        film.setDescription("Культовый фантастический фильм");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);
        film.setMpaRating(MpaRating.R);

        filmStorage.create(film);
        Optional<Film> userOptional = filmStorage.getFilmById(film.getId());
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(film1 ->
                        assertThat(film1).hasFieldOrPropertyWithValue("name", "Матрица")
                );
    }

    @Test
    public void testFindAll() {
        Collection<Film> films = filmStorage.findAll();
        assertEquals(5, films.size());
    }

    @Test
    public void testUpdateFilm() {
        Film film = new Film();
        film.setName("Матрица");
        film.setDescription("Культовый фантастический фильм");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);
        film.setMpaRating(MpaRating.R);

        filmStorage.create(film);

        film.setName("Матрица-2");
        filmStorage.update(film);

        Optional<Film> filmOptional = filmStorage.getFilmById(film.getId());
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(newFilm ->
                        assertThat(newFilm).hasFieldOrPropertyWithValue("name", "Матрица-2")
                );
    }

    @Test
    public void deleteFilm() {
        MpaRating mpa = MpaRating.fromId(4L);
        Film film = new Film();
        film.setName("Матрица");
        film.setDescription("Культовый фантастический фильм");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);
        film.setMpaRating(mpa);

        filmStorage.create(film);

        Long id = film.getId();
        filmStorage.deleteFilm(id);
        Optional<Film> filmOptional = filmStorage.getFilmById(id);
        assertThat(filmOptional).isEmpty();
    }
}
