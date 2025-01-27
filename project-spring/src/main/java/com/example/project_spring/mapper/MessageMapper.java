package com.example.project_spring.mapper;

import com.example.project_spring.dto.MessageDTO;
import com.example.project_spring.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageDTO toDto(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setSenderId(message.getSender().getId());
        dto.setReceiverId(message.getReceiver().getId());
        dto.setText(message.getText());
        dto.setSentDate(message.getSentDate());
        return dto;
    }

    public Message toEntity(MessageDTO dto) {
        Message message = new Message();
        message.setId(dto.getId());
        message.setText(dto.getText());
        message.setSentDate(dto.getSentDate());
        return message;
    }
}
