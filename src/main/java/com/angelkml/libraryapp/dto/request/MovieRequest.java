package com.angelkml.libraryapp.dto.request;

import java.util.List;

import com.angelkml.libraryapp.entity.Genre;
import com.angelkml.libraryapp.entity.MovieType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MovieRequest(@NotBlank String title, @NotBlank String barcode, boolean watched,
                            @NotNull MovieType type, @NotNull Genre genre, @NotNull Integer year,
                            List<String> actors) {
}
