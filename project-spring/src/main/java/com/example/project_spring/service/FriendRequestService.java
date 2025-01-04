package com.example.project_spring.service;

import com.example.project_spring.dto.FriendRequestDTO;

import java.util.List;

public interface FriendRequestService {
    public FriendRequestDTO sendFriendRequest(Long senderId, Long receiverId);
    public List<FriendRequestDTO> getReceivedRequests(Long userId);
}
