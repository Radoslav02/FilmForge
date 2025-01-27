package com.example.project_spring.controller;

import com.example.project_spring.dto.FavoriteMoviesDTO;
import com.example.project_spring.exception.ResourceNotFoundException;
import com.example.project_spring.service.FavoriteMoviesService;
import com.example.project_spring.service.impl.FavoriteMoviesServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite-movies")
@AllArgsConstructor
public class FavoriteMoviesController {

    private final FavoriteMoviesServiceImpl favoriteMoviesService;

    @PostMapping("/create-empty-list/{userId}/{categoryName}")
    public ResponseEntity<FavoriteMoviesDTO> createEmptyFavoriteMoviesList(
            @PathVariable Long userId,
            @PathVariable String categoryName) {

        FavoriteMoviesDTO favoriteMoviesDTO = favoriteMoviesService.createEmptyFavoriteMoviesList(userId, categoryName);
        return new ResponseEntity<>(favoriteMoviesDTO, HttpStatus.CREATED);
    }

    @PostMapping("/{favoriteMoviesId}/add-movie/{movieId}")
    public ResponseEntity<FavoriteMoviesDTO> addMovieToFavoriteList(
            @PathVariable Long favoriteMoviesId,
            @PathVariable Long movieId) {

        FavoriteMoviesDTO favoriteMoviesDTO = favoriteMoviesService.addMovieToFavoriteList(favoriteMoviesId, movieId);
        return new ResponseEntity<>(favoriteMoviesDTO, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<FavoriteMoviesDTO>> getFavoriteMoviesList(@RequestParam Long userId) {
        try {
            List<FavoriteMoviesDTO> favoriteMoviesList = favoriteMoviesService.getFavoriteMoviesList(userId);
            return new ResponseEntity<>(favoriteMoviesList, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/add-movie/{favoriteMoviesId}/{movieId}")
    public ResponseEntity<String> addMovieToFavorite(
            @PathVariable Long favoriteMoviesId,
            @PathVariable Long movieId) {
        try {
            favoriteMoviesService.addMovieToFavorite(favoriteMoviesId, movieId);
            return new ResponseEntity<>("Movie added to favorite list successfully", HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to add movie to favorite list", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("deleteList/{favoriteListId}")
    public ResponseEntity<String> deleteFavoriteList(@PathVariable Long favoriteListId) {
        favoriteMoviesService.deleteFavoriteList(favoriteListId);
        return ResponseEntity.ok("Favorite list with ID " + favoriteListId + " has been successfully deleted.");
    }

    @DeleteMapping("deleteMovieFromList/{movieId}/{listId}")
    public ResponseEntity<String> deleteFavoriteList(@PathVariable Long listId, @PathVariable Long movieId) {
        favoriteMoviesService.removeMovieFromList(listId, movieId);
        return ResponseEntity.ok("Movie with id: " + movieId + " has been successfully deleted.");
    }

}
