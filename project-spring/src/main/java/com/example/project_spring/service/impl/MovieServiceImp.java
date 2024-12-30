package com.example.project_spring.service.impl;

import com.example.project_spring.dto.MovieDTO;
import com.example.project_spring.entity.Category;
import com.example.project_spring.entity.Movie;
import com.example.project_spring.entity.User;
import com.example.project_spring.exception.ResourceNotFoundException;
import com.example.project_spring.mapper.MovieMapper;
import com.example.project_spring.repository.CategoryRepository;
import com.example.project_spring.repository.MovieRepository;
import com.example.project_spring.repository.UserRepository;
import com.example.project_spring.service.MovieService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

// MovieServiceImp
@Service
@AllArgsConstructor
public class MovieServiceImp implements MovieService {

    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public MovieDTO createMovie(MovieDTO movieDTO, MultipartFile image, Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User with given id not found: " + userId));

            Category category = categoryRepository.findById(movieDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category with given id not found: " + movieDTO.getCategoryId()));

            String imageUrl = null;
            if (image != null && !image.isEmpty()) {
                String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
                Path uploadPath = Paths.get("uploads/user_" + userId);
                Files.createDirectories(uploadPath);
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                imageUrl = "/uploads/user_" + userId + "/" + fileName;
            }

            Movie movie = new Movie();
            movie.setTitle(movieDTO.getTitle());
            movie.setDirector(movieDTO.getDirector());
            movie.setDescription(movieDTO.getDescription());
            movie.setReleaseDate(movieDTO.getReleaseDate());
            movie.setCategory(category);
            movie.setUser(user); // Associating the movie with the user
            movie.setImageUrl(imageUrl);

            movieRepository.save(movie);

            return MovieMapper.mapToMovieDTO(movie);
        } catch (IOException e) {
            throw new RuntimeException("Error occurred while uploading the image", e);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while creating the movie", e);
        }
    }
}