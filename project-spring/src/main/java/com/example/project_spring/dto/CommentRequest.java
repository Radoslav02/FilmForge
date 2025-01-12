package com.example.project_spring.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CommentRequest {
    private Long userId;
    private String text;
}

