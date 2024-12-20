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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {

    private Long id;
    private String title;
    private String director;
    private Long categoryId;
    private Long userId;
    private String description;
    private Date releaseDate;

}
