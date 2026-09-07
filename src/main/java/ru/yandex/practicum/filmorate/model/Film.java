package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;

@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    @JsonProperty("genres")
    private List<Genre> genre = new ArrayList<>();
    @JsonProperty("mpa")
    private MpaRating mpaRating;
    private Set<Long> likes = new HashSet<>();
}
