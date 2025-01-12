package com.example.project_spring.controller;

import com.example.project_spring.dto.MessageDTO;
import com.example.project_spring.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    public MessageDTO sendMessage(@RequestParam Long senderId, @RequestParam Long receiverId, @RequestParam String text) {
        return messageService.sendMessage(senderId, receiverId, text);
    }

    @GetMapping("/conversation")
    public List<MessageDTO> getMessages(@RequestParam Long senderId, @RequestParam Long receiverId) {
        return messageService.getMessagesBetweenUsers(senderId, receiverId);
    }
}

