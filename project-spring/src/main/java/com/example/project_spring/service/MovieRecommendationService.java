package com.example.project_spring.service;

import com.example.project_spring.dto.MovieDTO;
import com.example.project_spring.dto.MovieRecommendationDTO;

import java.util.Date;
import java.util.List;

public interface MovieRecommendationService {
    public MovieRecommendationDTO createRecommendation(Long movieId, Long recommenderId, Long receiverId);

    public List<MovieDTO> getRecommendations(Long receiverId);
}
