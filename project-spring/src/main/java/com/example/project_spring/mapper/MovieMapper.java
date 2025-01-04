package com.example.project_spring.mapper;

import com.example.project_spring.dto.CategoryDTO;
import com.example.project_spring.dto.MovieDTO;
import com.example.project_spring.entity.Category;
import com.example.project_spring.entity.Movie;
import com.example.project_spring.entity.User;

public class MovieMapper {

    public static Movie mapToMovie(MovieDTO movieDTO, User user, Category category, String imageUrl) {
        Movie movie = new Movie();
        movie.setTitle(movieDTO.getTitle());
        movie.setDirector(movieDTO.getDirector());
        movie.setReleaseDate(movieDTO.getReleaseDate());
        movie.setDescription(movieDTO.getDescription());
        movie.setCategory(category);
        movie.setUser(user);
        movie.setImageUrl(imageUrl);
        return movie;
    }

    public static MovieDTO mapToMovieDTO(Movie movie) {
        return new MovieDTO(
                movie.getId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getReleaseDate(),
                movie.getDescription(),
                movie.getCategory().getId(),
                movie.getImageUrl(),
                movie.getCategory().getName()
        );
    }

}
