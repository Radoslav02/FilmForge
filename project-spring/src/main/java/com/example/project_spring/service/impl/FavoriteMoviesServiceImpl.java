package com.example.project_spring.service.impl;

import com.example.project_spring.dto.FavoriteMoviesDTO;
import com.example.project_spring.dto.MovieDTO;
import com.example.project_spring.entity.FavoriteMovies;
import com.example.project_spring.entity.Movie;
import com.example.project_spring.entity.User;
import com.example.project_spring.exception.ResourceNotFoundException;
import com.example.project_spring.repository.FavoriteMoviesRepository;
import com.example.project_spring.repository.MovieRepository;
import com.example.project_spring.repository.UserRepository;
import com.example.project_spring.service.FavoriteMoviesService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FavoriteMoviesServiceImpl implements FavoriteMoviesService {
    private final UserRepository userRepository;
    private final FavoriteMoviesRepository favoriteMoviesRepository;
    private final MovieRepository movieRepository;

    @Override
    public FavoriteMoviesDTO createEmptyFavoriteMoviesList(Long userId, String categoryName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        FavoriteMovies favoriteMovies = new FavoriteMovies();
        favoriteMovies.setUser(user);
        favoriteMovies.setCategoryName(categoryName);
        favoriteMovies.setMovies(new ArrayList<>());

        FavoriteMovies savedFavoriteMovies = favoriteMoviesRepository.save(favoriteMovies);

        return new FavoriteMoviesDTO(
                savedFavoriteMovies.getId(),
                savedFavoriteMovies.getUser().getId(),
                savedFavoriteMovies.getCategoryName(),
                List.of()
        );
    }


    @Override
    public FavoriteMoviesDTO addMovieToFavoriteList(Long favoriteMoviesId, Long movieId) {
        FavoriteMovies favoriteMovies = favoriteMoviesRepository.findById(favoriteMoviesId)
                .orElseThrow(() -> new RuntimeException("Favorite movies list not found"));

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        if (!favoriteMovies.getMovies().contains(movie)) {
            favoriteMovies.getMovies().add(movie);
        } else {
            throw new RuntimeException("Movie already exists in the list");
        }

        FavoriteMovies updatedFavoriteMovies = favoriteMoviesRepository.save(favoriteMovies);

        List<MovieDTO> movieDTOList = updatedFavoriteMovies.getMovies().stream()
                .map(m -> new MovieDTO(
                        m.getId(),
                        m.getUser().getUsername(),
                        m.getTitle(),
                        m.getDirector(),
                        m.getReleaseDate(),
                        m.getDescription(),
                        m.getCategory().getId(),
                        m.getImageUrl(),
                        m.getCategory().getName(),
                        m.getAverageGrade()
                ))
                .collect(Collectors.toList());

        return new FavoriteMoviesDTO(
                updatedFavoriteMovies.getId(),
                updatedFavoriteMovies.getUser().getId(),
                updatedFavoriteMovies.getCategoryName(),
                movieDTOList
        );
    }

    @Override
    public List<FavoriteMoviesDTO> getFavoriteMoviesList(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));

        List<FavoriteMovies> favoriteMoviesList = favoriteMoviesRepository.findByUserId(userId);

        if(favoriteMoviesList.isEmpty()){
            throw new RuntimeException("Favorite movies list is empty");
        }
        return favoriteMoviesList.stream().
                map(list -> new FavoriteMoviesDTO(list.getId(), list.getUser().getId(), list.getCategoryName(), list.getMovies().stream().map(m -> new MovieDTO(
                                m.getId(),
                                m.getUser().getUsername(),
                                m.getTitle(),
                                m.getDirector(),
                                m.getReleaseDate(),
                                m.getDescription(),
                                m.getCategory().getId(),
                                m.getImageUrl(),
                                m.getCategory().getName(),
                                m.getAverageGrade()
                        ))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Override
    public void addMovieToFavorite(Long favoriteMoviesId, Long movieId) {

        FavoriteMovies favoriteMovies = favoriteMoviesRepository.findById(favoriteMoviesId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite movies list not found"));


        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie with id " + movieId + " not found"));

        
        if (!favoriteMovies.getMovies().contains(movie)) {
            favoriteMovies.getMovies().add(movie);

            // Spasavanje promena u bazi
            favoriteMoviesRepository.save(favoriteMovies);
        }
    }

    @Override
    public void deleteFavoriteList(Long favoriteListId) {
        FavoriteMovies favoriteMovies = favoriteMoviesRepository.findById(favoriteListId).orElseThrow(() -> new ResourceNotFoundException("Favorite movies list not found"));

        favoriteMoviesRepository.delete(favoriteMovies);
    }

    @Override
    public void removeMovieFromList(Long favoriteListId, Long movieId) {
        FavoriteMovies list = favoriteMoviesRepository.findById(favoriteListId).orElseThrow(() -> new ResourceNotFoundException("Favorite movies list not found"));

        List<Movie> movies = list.getMovies();

        Movie removeMovie = new Movie();
        if(!movies.isEmpty()){
            for (Movie movie : movies) {
                if (movie.getId().equals(movieId)) {
                    removeMovie = movie;
                }
            }
        }else{
            throw new ResourceNotFoundException("Favorite movies list is empty");
        }

        movies.remove(removeMovie);
    }


}

