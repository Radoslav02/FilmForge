package com.example.project_spring.service.impl;

import com.example.project_spring.dto.MovieDTO;
import com.example.project_spring.entity.Category;
import com.example.project_spring.entity.Movie;
import com.example.project_spring.entity.User;
import com.example.project_spring.exception.ResourceNotFoundException;
import com.example.project_spring.mapper.MovieMapper;
import com.example.project_spring.repository.CategoryRepository;
import com.example.project_spring.repository.MovieRepository;
import com.example.project_spring.repository.UserRepository;
import com.example.project_spring.service.MovieService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MovieServiceImp implements MovieService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private MovieRepository movieRepository;

    @Override
    public MovieDTO createMovie(MovieDTO movieDTO) {
        Category category = categoryRepository.findById(movieDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + movieDTO.getCategoryId()));

        User user = userRepository.findById(movieDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + movieDTO.getUserId()));

        Movie movie = MovieMapper.mapToMovie(movieDTO, category, user);
        Movie savedMovie = movieRepository.save(movie);

        return MovieMapper.mapToMovieDTO(savedMovie);
    }

    @Override
    public MovieDTO getMovieById(Long id) {
        return null;
    }

    @Override
    public List<MovieDTO> getAllMovies() {
        return List.of();
    }

    @Override
    public MovieDTO updateMovie(Long movieId, MovieDTO updatedMovieDTO) {
        return null;
    }

    @Override
    public void deleteMovie(Long movieId) {

    }


}
