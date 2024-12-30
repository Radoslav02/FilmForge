package com.example.project_spring.controller;

import com.example.project_spring.dto.MovieDTO;

import com.example.project_spring.entity.Category;
import com.example.project_spring.entity.User;
import com.example.project_spring.service.impl.MovieServiceImp;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;


@RestController
@RequestMapping("/api/movie")
@AllArgsConstructor
public class MovieController {

    private final MovieServiceImp movieService;

    @PostMapping("/addMovie")
    public ResponseEntity<String> addMovie(
            @RequestParam("title") String title,
            @RequestParam("director") String director,
            @RequestParam("releaseDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date releaseDate,
            @RequestParam("description") String description,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("userId") Long userId) {

        MovieDTO movieDTO = new MovieDTO(title, director, releaseDate, description, categoryId);
        movieService.createMovie(movieDTO, image, userId); // Ensure userId is passed to service

        return ResponseEntity.ok("Movie added successfully");
    }
}