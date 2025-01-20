package com.example.project_spring.repository;

import com.example.project_spring.dto.MovieRecommendationDTO;
import com.example.project_spring.entity.MovieRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRecommendationRepository extends JpaRepository<MovieRecommendation, Long> {

    List<MovieRecommendation> findByReceiverId(Long movieId);



}
