package com.angelkml.libraryapp.dto.response;

import java.util.List;

import com.angelkml.libraryapp.dto.MovieDto;
import com.angelkml.libraryapp.entity.Genre;
import com.angelkml.libraryapp.entity.MovieType;

public record MovieResponse(Long id, String title, String barcode, boolean watched,
                             MovieType type, Genre genre, Integer year, List<String> actors) {

    public static MovieResponse from(MovieDto dto) {
        return new MovieResponse(dto.id(), dto.title(), dto.barcode(), dto.watched(),
                dto.type(), dto.genre(), dto.year(), dto.actors());
    }
}
