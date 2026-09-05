package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MpaRating {
    private Long id;
    private String name;

    public static final MpaRating G = new MpaRating(1L, "G");
    public static final MpaRating PG = new MpaRating(2L, "PG");
    public static final MpaRating PG_13 = new MpaRating(3L, "PG-13");
    public static final MpaRating R = new MpaRating(4L, "R");
    public static final MpaRating NC_17 = new MpaRating(5L, "NC-17");

    public static MpaRating fromId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id не может быть null");
        }
        for (MpaRating rating : getKnownRatings()) {
            if (rating.getId().equals(id)) {
                return rating;
            }
        }
        throw new NotFoundException("Рейтинг с id=" + id + " не найден");
    }

    private static List<MpaRating> getKnownRatings() {
        return List.of(G, PG, PG_13, R, NC_17);
    }
}