package com.angelkml.libraryapp.repository;

import java.util.Optional;

import com.angelkml.libraryapp.entity.Movie;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    Optional<Movie> findByBarcode(String barcode);

    boolean existsByBarcode(String barcode);
}
