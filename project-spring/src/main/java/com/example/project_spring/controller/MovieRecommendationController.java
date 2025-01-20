package com.example.project_spring.controller;

import com.example.project_spring.dto.MovieDTO;
import com.example.project_spring.dto.MovieRecommendationDTO;
import com.example.project_spring.service.impl.MovieRecommendationServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/recommendations")
public class MovieRecommendationController {

    private final MovieRecommendationServiceImpl movieRecommendationServiceImpl;

    @PostMapping("/sendRecommendation/{movieId}/{recommenderId}/{receiverId}")
    public ResponseEntity<MovieRecommendationDTO> createMovieRecommendation(
            @PathVariable Long movieId,
            @PathVariable Long recommenderId,
            @PathVariable Long receiverId) {
        MovieRecommendationDTO createdRecommendation = movieRecommendationServiceImpl.createRecommendation(
                movieId, recommenderId, receiverId);

        return new ResponseEntity<>(createdRecommendation, HttpStatus.CREATED);
    }

    @GetMapping("/getRecommendations/{receiverId}")
    public ResponseEntity<List<MovieDTO>> getRecommendations(@PathVariable Long receiverId) {
        List<MovieDTO> recommendations = movieRecommendationServiceImpl.getRecommendations(receiverId);
        return ResponseEntity.ok(recommendations);
    }
}
