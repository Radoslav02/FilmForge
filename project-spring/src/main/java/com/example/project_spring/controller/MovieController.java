package com.example.project_spring.controller;

import com.example.project_spring.dto.MovieDTO;

import com.example.project_spring.entity.Category;
import com.example.project_spring.entity.User;
import com.example.project_spring.service.impl.MovieServiceImp;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@AllArgsConstructor
@RestController
@RequestMapping("/api/movie")
public class MovieController {

    MovieServiceImp movieService;

    @PostMapping
    public ResponseEntity<MovieDTO> createMovie(@RequestBody MovieDTO movieDTO) {
        // Extract userId and categoryId from the MovieDTO if they are included
        MovieDTO savedMovie = movieService.createMovie(movieDTO);
        return new ResponseEntity<>(savedMovie, HttpStatus.CREATED);
    }


}
