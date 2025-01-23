package com.example.project_spring.repository;

import com.example.project_spring.dto.MovieDTO;
import com.example.project_spring.entity.Movie;
import com.example.project_spring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByUser(User user);


}
