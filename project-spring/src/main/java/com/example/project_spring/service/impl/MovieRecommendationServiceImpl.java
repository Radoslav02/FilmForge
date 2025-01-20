package com.example.project_spring.service.impl;

import com.example.project_spring.dto.MovieDTO;
import com.example.project_spring.dto.MovieRecommendationDTO;
import com.example.project_spring.entity.Movie;
import com.example.project_spring.entity.MovieRecommendation;
import com.example.project_spring.entity.User;
import com.example.project_spring.exception.ResourceNotFoundException;
import com.example.project_spring.repository.MovieRecommendationRepository;
import com.example.project_spring.repository.MovieRepository;
import com.example.project_spring.repository.UserRepository;
import com.example.project_spring.service.MovieRecommendationService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MovieRecommendationServiceImpl implements MovieRecommendationService {

    private final MovieRecommendationRepository movieRecommendationRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;

    @Override
    public MovieRecommendationDTO createRecommendation(Long movieId, Long recommenderId, Long receiverId) {
        // Use findById() to check the recommender
        Optional<User> recommender = userRepository.findById(recommenderId);
        if (!recommender.isPresent()) {
            throw new ResourceNotFoundException("Recommender not found");
        }

        // Use findById() to check the movie
        Optional<Movie> movie = movieRepository.findById(movieId);
        if (!movie.isPresent()) {
            throw new ResourceNotFoundException("Movie not found");
        }

        // Use findById() to check the receiver
        Optional<User> receiver = userRepository.findById(receiverId);
        if (!receiver.isPresent()) {
            throw new ResourceNotFoundException("Receiver not found");
        }

        // Create new recommendation
        Date now = new Date();

        MovieRecommendation movieRecommendation = new MovieRecommendation();
        movieRecommendation.setRecommender(recommender.get());
        movieRecommendation.setReceiver(receiver.get());
        movieRecommendation.setMovie(movie.get());
        movieRecommendation.setRecommendationDate(now);

        // Save and return the recommendation DTO
        MovieRecommendation savedRecommendation = movieRecommendationRepository.save(movieRecommendation);

        MovieRecommendationDTO savedDTO = new MovieRecommendationDTO();
        savedDTO.setId(savedRecommendation.getId());
        savedDTO.setRecommenderId(savedRecommendation.getRecommender().getId());
        savedDTO.setReceiverId(savedRecommendation.getReceiver().getId());
        savedDTO.setMovieId(savedRecommendation.getMovie().getId());
        savedDTO.setRecommendationDate(savedRecommendation.getRecommendationDate());

        return savedDTO;
    }

    @Override
    public List<MovieDTO> getRecommendations(Long receiverId) {
        userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        List<MovieRecommendation> recommendations = movieRecommendationRepository.findByReceiverId(receiverId);
        return recommendations.stream()
                .map(recommendation -> {
                    Movie movie = recommendation.getMovie();
                    return new MovieDTO(
                            movie.getId(),
                            movie.getUser().getUsername(),
                            movie.getTitle(),
                            movie.getDirector(),
                            movie.getReleaseDate(),
                            movie.getDescription(),
                            movie.getCategory().getId(),
                            movie.getImageUrl(),
                            movie.getCategory().getName(),
                            movie.getAverageGrade()
                    );
                })
                .toList();
    }


}
