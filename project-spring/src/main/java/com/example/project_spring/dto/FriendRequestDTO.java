package com.example.project_spring.dto;

import com.example.project_spring.entity.FriendRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestDTO {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private boolean accepted;
    private Date requestDate;
    private String senderName;
    private String senderSurname;
    private String senderUsername;
    private Long friendId;

    public FriendRequestDTO(Long id, Long senderId, Long receiverId, Date requestDate, String senderUsername, String senderName, String senderSurname, Long friendId) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.requestDate = requestDate;
        this.senderUsername = senderUsername;
        this.senderName = senderName;
        this.senderSurname = senderSurname;
        this.friendId = friendId;
    }



    public FriendRequestDTO(FriendRequest request) {
        this.id = request.getId();
        this.senderId = request.getSender() != null ? request.getSender().getId() : null;
        this.receiverId = request.getReceiver() != null ? request.getReceiver().getId() : null;
        this.accepted = request.isAccepted();
        this.requestDate = request.getRequestDate();


        if (request.getSender() != null) {
            this.senderName = request.getSender().getFirstName();
            this.senderSurname = request.getSender().getLastName();
            this.senderUsername = request.getSender().getUsername();
        }
    }
}
