package com.example.project_spring.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieRecommendationDTO {
    private Long id;
    private Long recommenderId;
    private Long receiverId;
    private Long movieId;
    private Date recommendationDate;

}
