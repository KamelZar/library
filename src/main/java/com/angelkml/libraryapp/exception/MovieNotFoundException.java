package com.angelkml.libraryapp.exception;

public class MovieNotFoundException extends RuntimeException {

    public MovieNotFoundException(Long id) {
        super("Movie not found: " + id);
    }
}
