package com.example.project_spring.dto;

import lombok.Data;

import java.util.Date;

@Data
public class MessageDTO {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String text;
    private Date sentDate;
}
