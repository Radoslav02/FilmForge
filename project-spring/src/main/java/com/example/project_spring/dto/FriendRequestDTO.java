package com.example.project_spring.dto;

import com.example.project_spring.entity.FriendRequest;
import com.example.project_spring.mapper.UserMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Data
public class FriendRequestDTO {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private boolean accepted;
    private Date requestDate;
    private String senderName; // Add sender's name
    private String senderSurname; // Add sender's surname
    private String senderUsername; // Add sender's username

    public FriendRequestDTO(FriendRequest request) {
        this.id = request.getId();
        this.senderId = request.getSender() != null ? request.getSender().getId() : null;
        this.receiverId = request.getReceiver() != null ? request.getReceiver().getId() : null;
        this.accepted = request.isAccepted();
        this.requestDate = request.getRequestDate();

        // Check if sender is not null before accessing properties
        if (request.getSender() != null) {
            this.senderName = request.getSender().getFirstName();
            this.senderSurname = request.getSender().getLastName();
            this.senderUsername = request.getSender().getUsername();
        }
    }



    public FriendRequestDTO() {
        // Default constructor, just in case
    }
}
