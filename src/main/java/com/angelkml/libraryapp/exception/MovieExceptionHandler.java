package com.angelkml.libraryapp.exception;

import java.util.Map;

import com.angelkml.libraryapp.controller.MovieController;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = MovieController.class)
public class MovieExceptionHandler {

    @ExceptionHandler(MovieNotFoundException.class)
    ResponseEntity<Map<String, String>> handleNotFound(MovieNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(DuplicateBarcodeException.class)
    ResponseEntity<Map<String, String>> handleDuplicate(DuplicateBarcodeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }
}
