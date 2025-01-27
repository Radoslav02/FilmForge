package com.example.project_spring.mapper;

import com.example.project_spring.dto.FriendRequestDTO;
import com.example.project_spring.entity.FriendRequest;
import com.example.project_spring.entity.User;

public class FriendRequestMapper {

    public static FriendRequestDTO toDTO(FriendRequest friendRequest) {
        if (friendRequest == null) {
            return null;
        }

        FriendRequestDTO dto = new FriendRequestDTO();
        dto.setId(friendRequest.getId());
        dto.setSenderId(friendRequest.getSender() != null ? friendRequest.getSender().getId() : null);
        dto.setReceiverId(friendRequest.getReceiver() != null ? friendRequest.getReceiver().getId() : null);
        dto.setAccepted(friendRequest.isAccepted());
        dto.setRequestDate(friendRequest.getRequestDate());

        if (friendRequest.getSender() != null) {
            dto.setSenderName(friendRequest.getSender().getFirstName());
            dto.setSenderSurname(friendRequest.getSender().getLastName());
            dto.setSenderUsername(friendRequest.getSender().getUsername());
        }

        return dto;
    }


    public static FriendRequest toEntity(FriendRequestDTO dto, User sender, User receiver) {
        if (dto == null) {
            return null;
        }

        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setId(dto.getId());
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        friendRequest.setAccepted(dto.isAccepted());
        friendRequest.setRequestDate(dto.getRequestDate());

        return friendRequest;
    }
}
