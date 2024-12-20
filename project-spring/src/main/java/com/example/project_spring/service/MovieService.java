package com.example.project_spring.service;

import com.example.project_spring.dto.MovieDTO;
import com.example.project_spring.entity.Category;
import com.example.project_spring.entity.User;

import java.util.List;

public interface MovieService {
    MovieDTO createMovie(MovieDTO movieDTO);

    MovieDTO getMovieById(Long id);

    List<MovieDTO> getAllMovies();

    MovieDTO updateMovie(Long movieId,MovieDTO updatedMovieDTO);

    void deleteMovie(Long movieId);
}
