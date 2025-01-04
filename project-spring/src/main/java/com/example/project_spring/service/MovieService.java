package com.example.project_spring.service;

import com.example.project_spring.dto.MovieDTO;
import com.example.project_spring.entity.Category;
import com.example.project_spring.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MovieService {

    MovieDTO createMovie(MovieDTO movieDTO, MultipartFile image, Long userId);

    public List<MovieDTO> getMoviesByUserId(Long userId);
}
