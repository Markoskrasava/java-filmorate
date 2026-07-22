package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final List<Mpa> mpaList = Arrays.asList(
            new Mpa(1L, "G"),
            new Mpa(2L, "PG"),
            new Mpa(3L, "PG-13"),
            new Mpa(4L, "R"),
            new Mpa(5L, "NC-17")
    );

    @GetMapping
    public List<Mpa> getAllMpa() {
        return mpaList;
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable Long id) {
        return mpaList.stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
    }
}
