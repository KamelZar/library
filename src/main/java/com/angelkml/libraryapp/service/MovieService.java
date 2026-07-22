package com.angelkml.libraryapp.service;

import java.util.List;

import com.angelkml.libraryapp.dto.MovieDto;
import com.angelkml.libraryapp.entity.Genre;
import com.angelkml.libraryapp.entity.Movie;
import com.angelkml.libraryapp.entity.MovieType;
import com.angelkml.libraryapp.exception.DuplicateBarcodeException;
import com.angelkml.libraryapp.exception.MovieNotFoundException;
import com.angelkml.libraryapp.repository.MovieRepository;

import org.springframework.stereotype.Service;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<MovieDto> findAll(String titleFilter, MovieType type, Genre genre) {
        return movieRepository.findAll().stream()
                .filter(movie -> titleFilter == null || titleFilter.isBlank()
                        || movie.getTitle().toLowerCase().contains(titleFilter.toLowerCase()))
                .filter(movie -> type == null || movie.getType() == type)
                .filter(movie -> genre == null || movie.getGenre() == genre)
                .map(this::toDto)
                .toList();
    }

    public MovieDto findById(Long id) {
        return toDto(getOrThrow(id));
    }

    public MovieDto create(MovieDto input) {
        if (movieRepository.existsByBarcode(input.barcode())) {
            throw new DuplicateBarcodeException(input.barcode());
        }
        Movie saved = movieRepository.save(new Movie(input.title(), input.barcode(), input.watched(),
                input.type(), input.genre(), input.year(), input.actors()));
        return toDto(saved);
    }

    public MovieDto update(Long id, MovieDto input) {
        Movie movie = getOrThrow(id);

        movieRepository.findByBarcode(input.barcode())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new DuplicateBarcodeException(input.barcode());
                });

        movie.setTitle(input.title());
        movie.setBarcode(input.barcode());
        movie.setWatched(input.watched());
        movie.setType(input.type());
        movie.setGenre(input.genre());
        movie.setYear(input.year());
        movie.setActors(input.actors());
        return toDto(movieRepository.save(movie));
    }

    public void delete(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new MovieNotFoundException(id);
        }
        movieRepository.deleteById(id);
    }

    private Movie getOrThrow(Long id) {
        return movieRepository.findById(id).orElseThrow(() -> new MovieNotFoundException(id));
    }

    private MovieDto toDto(Movie movie) {
        return new MovieDto(movie.getId(), movie.getTitle(), movie.getBarcode(), movie.isWatched(),
                movie.getType(), movie.getGenre(), movie.getYear(), movie.getActors());
    }
}
