package com.example.project_spring.dto;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteMoviesDTO {
    private Long id;
    private Long userId;
    private String categoryName;
    private List<MovieDTO> movies;

    public FavoriteMoviesDTO(Long id, Long userId, String categoryName) {
        this.id = id;
        this.userId = userId;
        this.categoryName = categoryName;
    }
}
