package com.angelkml.libraryapp.controller;

import java.util.List;

import com.angelkml.libraryapp.dto.MovieDto;
import com.angelkml.libraryapp.dto.request.MovieRequest;
import com.angelkml.libraryapp.dto.response.MovieResponse;
import com.angelkml.libraryapp.entity.Genre;
import com.angelkml.libraryapp.entity.MovieType;
import com.angelkml.libraryapp.service.MovieService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    List<MovieResponse> list(@RequestParam(required = false) String title,
                              @RequestParam(required = false) MovieType type,
                              @RequestParam(required = false) Genre genre) {
        return movieService.findAll(title, type, genre).stream().map(MovieResponse::from).toList();
    }

    @GetMapping("/{id}")
    MovieResponse get(@PathVariable Long id) {
        return MovieResponse.from(movieService.findById(id));
    }

    @PostMapping
    ResponseEntity<MovieResponse> create(@Valid @RequestBody MovieRequest request) {
        MovieDto created = movieService.create(toDto(null, request));
        return ResponseEntity.status(HttpStatus.CREATED).body(MovieResponse.from(created));
    }

    @PutMapping("/{id}")
    MovieResponse update(@PathVariable Long id, @Valid @RequestBody MovieRequest request) {
        MovieDto updated = movieService.update(id, toDto(id, request));
        return MovieResponse.from(updated);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id) {
        movieService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private MovieDto toDto(Long id, MovieRequest request) {
        return new MovieDto(id, request.title(), request.barcode(), request.watched(),
                request.type(), request.genre(), request.year(), request.actors());
    }
}
