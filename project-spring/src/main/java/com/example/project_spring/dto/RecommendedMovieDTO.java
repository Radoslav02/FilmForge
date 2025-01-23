package com.example.project_spring.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RecommendedMovieDTO {

    private Long id;
    private String title;
    private String director;
    private Date releaseDate;
    private String description;
    private Long categoryId;
    private String imageUrl;
    private String categoryName;
    private String username;
    private double averageGrade;
    private String senderName;

    public RecommendedMovieDTO(Long id, String username, String title, String director, Date releaseDate, String description, Long categoryId, String imageUrl, String categoryName, double averageGrade, String senderName) {
        this.id = id;
        this.username = username;
        this.categoryId = categoryId;
        this.description = description;
        this.director = director;
        this.releaseDate = releaseDate;
        this.title = title;
        this.imageUrl = imageUrl;
        this.categoryName = categoryName;
        this.averageGrade = averageGrade;
        this.senderName = senderName;
    }

}
