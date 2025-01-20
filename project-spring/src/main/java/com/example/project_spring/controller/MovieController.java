package com.example.project_spring.controller;

import com.example.project_spring.dto.CommentDTO;
import com.example.project_spring.dto.CommentRequest;
import com.example.project_spring.dto.MovieDTO;

import com.example.project_spring.entity.Category;
import com.example.project_spring.entity.Comment;
import com.example.project_spring.entity.Movie;
import com.example.project_spring.entity.User;
import com.example.project_spring.exception.ResourceNotFoundException;
import com.example.project_spring.repository.MovieRepository;
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
import java.util.List;


@RestController
@RequestMapping("/api/movie")
@AllArgsConstructor
public class MovieController {

    private final MovieServiceImp movieService;
    private final MovieRepository movieRepository;

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

    @PostMapping("/movies/friends/{userId}")
    public ResponseEntity<List<MovieDTO>> getMoviesByFriends(@PathVariable Long userId){
        List<MovieDTO> movies = movieService.getMoviesByFriends(userId);
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/getAllMovies")
    public ResponseEntity<List<MovieDTO>> getAllMovies(){
        List<MovieDTO> movies = movieService.getAllMovies();
        return ResponseEntity.ok(movies);
    }

    @PostMapping("/{movieId}/grade")
    public ResponseEntity<String> addGrade(
            @PathVariable Long movieId,
            @RequestParam("userId") Long userId,
            @RequestParam("value") int value) {

        if (value < 1 || value > 5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Grade value must be between 1 and 5.");
        }

        movieService.addGradeToMovie(movieId, userId, value);

        return ResponseEntity.ok("Grade added successfully");
    }

    @PostMapping("/{movieId}/comment")
    public ResponseEntity<Comment> addComment(
            @PathVariable Long movieId,
            @RequestBody CommentRequest commentRequest // Koristi JSON telo za komentar
    ) {
        Comment comment = movieService.addComment(movieId, commentRequest.getUserId(), commentRequest.getText());
        return ResponseEntity.ok(comment);
    }

    // Endpoint za dobijanje komentara za film
    @GetMapping("/{movieId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsByMovie(@PathVariable Long movieId) {
        List<CommentDTO> comments = movieService.getCommentsByMovie(movieId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{id}/edit")
    public ResponseEntity<String> updateMovie(
            @PathVariable("id") Long id,
            @RequestParam("title") String title,
            @RequestParam("director") String director,
            @RequestParam("releaseDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date releaseDate,
            @RequestParam("description") String description,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("userId") Long userId) {

        // Kreiramo MovieDTO objekat sa podacima sa klijentske strane
        MovieDTO movieDTO = new MovieDTO(title, director, releaseDate, description, categoryId);
        movieDTO.setId(id); // Setujemo ID filma koji se ažurira

        try {
            // Pozivamo servis koji obavlja ažuriranje filma
            movieService.updateMovie(movieDTO, image, userId);
            return ResponseEntity.ok("Movie updated successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update movie");
        }
    }

    @GetMapping("/{id}/getMovie")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable("id") Long id) {
        try {
            Movie movie = movieRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
            MovieDTO movieDTO = new MovieDTO(movie.getId(), movie.getTitle(), movie.getDirector(),
                    movie.getReleaseDate(), movie.getDescription(), movie.getCategory().getId(),
                    movie.getImageUrl(), movie.getCategory().getName());
            return ResponseEntity.ok(movieDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/deleteMovie/{movieId}")
    public ResponseEntity<String> deleteMovie(@PathVariable Long movieId) {
        try{
            movieService.deleteMovie(movieId);
            return ResponseEntity.ok("Movie deleted successfully");
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}