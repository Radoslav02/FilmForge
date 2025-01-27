package com.example.project_spring.service.impl;

import com.example.project_spring.dto.CommentDTO;
import com.example.project_spring.dto.MovieDTO;
import com.example.project_spring.entity.*;
import com.example.project_spring.exception.ResourceNotFoundException;
import com.example.project_spring.mapper.MovieMapper;
import com.example.project_spring.repository.*;
import com.example.project_spring.service.MovieService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

// MovieServiceImp
@Service
@AllArgsConstructor
public class MovieServiceImp implements MovieService {

    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;


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
            movie.setUser(user);
            movie.setImageUrl(imageUrl);

            movieRepository.save(movie);

            return MovieMapper.mapToMovieDTO(movie);
        } catch (IOException e) {
            throw new RuntimeException("Error occurred while uploading the image", e);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while creating the movie", e);
        }
    }

    @Override
    public List<MovieDTO> getMoviesByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Movie> movies = movieRepository.findByUser(user);

        return movies.stream()
                .map(movie -> new MovieDTO(
                        movie.getId(),
                        movie.getTitle(),
                        movie.getDirector(),
                        movie.getReleaseDate(),
                       movie.getDescription(),
                        movie.getCategory().getId(),
                        movie.getImageUrl(),
                        movie.getCategory().getName()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<MovieDTO> getMoviesByFriends(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        List<User> friends = user.getReceivedRequests().stream()
                .filter(FriendRequest::isAccepted)
                .map(FriendRequest::getSender)
                .collect(Collectors.toList());

        friends.addAll(user.getSentRequests().stream()
                .filter(FriendRequest::isAccepted)
                .map(FriendRequest::getReceiver)
                .collect(Collectors.toList()));

        List<Movie> movies = friends.stream()
                .flatMap(friend -> movieRepository.findByUser(friend).stream())
                .collect(Collectors.toList());

        movies.sort((m1,m2) -> m2.getReleaseDate().compareTo(m1.getReleaseDate()));

        return movies.stream()
                .map(movie -> new MovieDTO(
                        movie.getId(),
                        movie.getTitle(),
                        movie.getDirector(),
                        movie.getReleaseDate(),
                        movie.getDescription(),
                        movie.getCategory().getId(),
                        movie.getImageUrl(),
                        movie.getCategory().getName(),
                        movie.getUser().getUsername(),
                        movie.getAverageGrade()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addGradeToMovie(Long movieId, Long userId, int value) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Optional<Grade> existingGrade = movie.getGrades().stream()
                .filter(grade -> grade.getUser().getId().equals(userId))
                .findFirst();

        if (existingGrade.isPresent()) {
            Grade grade = existingGrade.get();
            grade.setValue(value);
        } else {
            Grade grade = new Grade(user, movie, value);
            movie.getGrades().add(grade);
        }

        movieRepository.save(movie);
    }

    @Transactional
    public Comment addComment(Long movieId, Long userId, String text) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment();
        comment.setContent(text);
        comment.setMovie(movie);
        comment.setUser(user);

        commentRepository.save(comment);

        movie.getComments().add(comment);

        movieRepository.save(movie);

        return comment;
    }

    @Transactional
    public List<CommentDTO> getCommentsByMovie(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        List<CommentDTO> commentDTOs = movie.getComments().stream()
                .map(comment -> new CommentDTO(comment.getUser().getUsername(), comment.getContent()))
                .collect(Collectors.toList());

        return commentDTOs;
    }

    @Transactional
    public void updateMovie(MovieDTO movieDTO, MultipartFile image, Long userId) {
        Movie movie = movieRepository.findById(movieDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieDTO.getId()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Category category = categoryRepository.findById(movieDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + movieDTO.getCategoryId()));

        movie.setTitle(movieDTO.getTitle());
        movie.setDirector(movieDTO.getDirector());
        movie.setReleaseDate(movieDTO.getReleaseDate());
        movie.setDescription(movieDTO.getDescription());
        movie.setCategory(category);
        movie.setUser(user);

        if (image != null && !image.isEmpty()) {
            try {
                String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
                Path uploadPath = Paths.get("uploads/user_" + userId);
                Files.createDirectories(uploadPath);
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                movie.setImageUrl("/uploads/user_" + userId + "/" + fileName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save image", e);
            }
        }

        movieRepository.save(movie);
    }

    @Transactional
    public void deleteMovie(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));

        movieRepository.delete(movie);
    }


    @Transactional
    public List<MovieDTO> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();

        return movies.stream()
                .map(movie -> new MovieDTO(
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
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public JasperPrint generateTop10MoviesReport() {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("jasperreports/MoviesReport.jrxml");
            if (inputStream == null) {
                throw new FileNotFoundException("Report template 'MoviesReport.jrxml' not found.");
            }
            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);

            List<Movie> movies = movieRepository.findAll();
            if (movies.isEmpty()) {
                throw new RuntimeException("No movies found in the database.");
            }

            List<MovieDTO> topMovies = movies.stream()
                    .sorted(Comparator.comparingDouble(Movie::getAverageGrade).reversed())
                    .limit(10)
                    .map(movie -> new MovieDTO(
                            movie.getCategory().getName(),
                            movie.getTitle(),
                            movie.getDirector(),
                            movie.getReleaseDate(),
                            movie.getAverageGrade()
                    ))
                    .collect(Collectors.toList());

            if (topMovies.isEmpty()) {
                throw new RuntimeException("No top movies to include in the report.");
            }

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(topMovies);

            Map<String, Object> parameters = new HashMap<>();

            return JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating report: " + e.getMessage(), e);
        }
    }






}