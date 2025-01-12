package com.example.project_spring.service;

import com.example.project_spring.dto.FriendRequestDTO;
import com.example.project_spring.entity.User;

import java.util.List;

public interface FriendRequestService {
    public FriendRequestDTO sendFriendRequest(Long senderId, Long receiverId);
    public List<FriendRequestDTO> getReceivedRequests(Long userId);
    public List<User> getFriends(Long userId);
}
