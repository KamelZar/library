package com.angelkml.libraryapp.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String barcode;

    private boolean watched;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovieType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genre genre;

    @Column(name = "release_year", nullable = false)
    private Integer year;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "movie_actors", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "actor_name")
    private List<String> actors = new ArrayList<>();

    protected Movie() {
    }

    public Movie(String title, String barcode, boolean watched, MovieType type, Genre genre, Integer year, List<String> actors) {
        this.title = title;
        this.barcode = barcode;
        this.watched = watched;
        this.type = type;
        this.genre = genre;
        this.year = year;
        this.actors = actors != null ? actors : new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public MovieType getType() {
        return type;
    }

    public void setType(MovieType type) {
        this.type = type;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public List<String> getActors() {
        return actors;
    }

    public void setActors(List<String> actors) {
        this.actors = actors != null ? actors : new ArrayList<>();
    }
}
