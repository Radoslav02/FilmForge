package com.example.project_spring.service;

import com.example.project_spring.dto.FavoriteMoviesDTO;
import com.example.project_spring.dto.MovieDTO;
import com.example.project_spring.entity.FavoriteMovies;

import java.util.List;

public interface FavoriteMoviesService {

    public FavoriteMoviesDTO createEmptyFavoriteMoviesList(Long userId, String categoryName);

    public FavoriteMoviesDTO addMovieToFavoriteList(Long favoriteMoviesId, Long movieId);

    public List<FavoriteMoviesDTO> getFavoriteMoviesList(Long userId);

    public void addMovieToFavorite(Long favoriteMoviesId, Long movieId);

    public void deleteFavoriteList(Long favoriteListId);

    public void removeMovieFromList(Long favoriteListId, Long movieId);
}
