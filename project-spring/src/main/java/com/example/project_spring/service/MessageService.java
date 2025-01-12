package com.example.project_spring.service;

import com.example.project_spring.dto.MessageDTO;

import java.util.List;

public interface MessageService {
    MessageDTO sendMessage(Long senderId, Long receiverId, String text);
    List<MessageDTO> getMessagesBetweenUsers(Long senderId, Long receiverId);
}
