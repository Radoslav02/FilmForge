package com.example.project_spring.service.impl;

import com.example.project_spring.dto.MessageDTO;
import com.example.project_spring.entity.Message;
import com.example.project_spring.entity.User;
import com.example.project_spring.mapper.MessageMapper;
import com.example.project_spring.repository.MessageRepository;
import com.example.project_spring.repository.UserRepository;
import com.example.project_spring.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;

    @Override
    public MessageDTO sendMessage(Long senderId, Long receiverId, String text) {
        User sender = userRepository.findById(senderId).orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setText(text);
        message.setSentDate(new Date());

        Message savedMessage = messageRepository.save(message);
        return messageMapper.toDto(savedMessage);
    }

    @Override
    public List<MessageDTO> getMessagesBetweenUsers(Long senderId, Long receiverId) {
        // Fetching messages between users sorted by sentDate (ascending)
        List<Message> messages = messageRepository.findBySenderIdAndReceiverId(senderId, receiverId);
        messages.addAll(messageRepository.findBySenderIdAndReceiverId(receiverId, senderId));

        // Sort by sentDate
        messages.sort(Comparator.comparing(Message::getSentDate));

        return messages.stream()
                .map(messageMapper::toDto)
                .collect(Collectors.toList());
    }
}
