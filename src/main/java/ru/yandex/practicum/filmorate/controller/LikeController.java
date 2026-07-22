package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("films")
public class LikeController {
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {

    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {

    }

    @GetMapping("/popular")
    public List<Object> getPopularFilms(@RequestParam(required = false, defaultValue = "10") Integer count) {
        return Collections.emptyList();
    }
}
