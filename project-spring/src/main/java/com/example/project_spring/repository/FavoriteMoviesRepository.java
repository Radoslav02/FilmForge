package com.example.project_spring.repository;

import com.example.project_spring.entity.FavoriteMovies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteMoviesRepository extends JpaRepository<FavoriteMovies, Long> {
    public List<FavoriteMovies> findByUserId(Long userId);
}
