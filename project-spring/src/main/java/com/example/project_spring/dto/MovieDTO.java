package com.example.project_spring.dto;

import com.example.project_spring.entity.Category;
import com.example.project_spring.entity.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

// MovieDTO
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovieDTO {
    private String title;
    private String director;
    private Date releaseDate;
    private String description;
    private Long categoryId;
}

