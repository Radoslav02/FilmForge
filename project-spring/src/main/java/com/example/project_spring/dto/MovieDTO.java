package com.example.project_spring.dto;

import com.example.project_spring.entity.Category;
import com.example.project_spring.entity.Grade;
import com.example.project_spring.entity.Movie;
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

    public MovieDTO(String title, String director, Date releaseDate, String description, Long categoryId) {
        this.categoryId = categoryId;
        this.description = description;
        this.director = director;
        this.releaseDate = releaseDate;
        this.title = title;
    }

    public MovieDTO(Long id, String title, String director, Date releaseDate, String description, Long categoryId, String imageUrl, String categoryName) {
        this.id = id;
        this.categoryId = categoryId;
        this.description = description;
        this.director = director;
        this.releaseDate = releaseDate;
        this.title = title;
        this.imageUrl = imageUrl;
        this.categoryName = categoryName;

    }

        public MovieDTO(Long id, String username, String title, String director, Date releaseDate, String description, Long categoryId, String imageUrl, String categoryName, double averageGrade) {
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
        }

    public MovieDTO(String categoryName, String title, String director, Date releaseDate, double averageGrade) {
        this.categoryName = categoryName; // Naziv kategorije za grupisanje
        this.title = title;              // Naslov filma
        this.director = director;        // Režiser filma
        this.releaseDate = releaseDate;  // Datum izlaska
        this.averageGrade = averageGrade; // Prosečna ocena filma
    }

}