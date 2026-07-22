package com.angelkml.libraryapp.dto;

import java.util.List;

import com.angelkml.libraryapp.entity.Genre;
import com.angelkml.libraryapp.entity.MovieType;

/**
 * Représentation d'un film manipulée par la couche service, indépendante de l'entité JPA
 * et des POJO request/response du contrôleur.
 */
public record MovieDto(Long id, String title, String barcode, boolean watched,
                        MovieType type, Genre genre, Integer year, List<String> actors) {
}
