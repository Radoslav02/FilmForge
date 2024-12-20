package com.example.project_spring.mapper;

import com.example.project_spring.dto.CategoryDTO;
import com.example.project_spring.dto.MovieDTO;
import com.example.project_spring.entity.Category;
import com.example.project_spring.entity.Movie;
import com.example.project_spring.entity.User;

public class MovieMapper {

    public static Movie mapToMovie(MovieDTO movieDTO, Category category, User user) {
        Movie movie = new Movie();
        movie.setTitle(movieDTO.getTitle());
        movie.setDirector(movieDTO.getDirector());
        movie.setDescription(movieDTO.getDescription());
        movie.setReleaseDate(movieDTO.getReleaseDate());
        movie.setCategory(category);
        movie.setUser(user);
        return movie;
    }

    public static MovieDTO mapToMovieDTO(Movie movie) {
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setId(movie.getId()); // If you need to return the generated id
        movieDTO.setTitle(movie.getTitle());
        movieDTO.setDirector(movie.getDirector());
        movieDTO.setDescription(movie.getDescription());
        movieDTO.setReleaseDate(movie.getReleaseDate());
        movieDTO.setCategoryId(movie.getCategory().getId());
        movieDTO.setUserId(movie.getUser().getId());
        return movieDTO;
    }

}
