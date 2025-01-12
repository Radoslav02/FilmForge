package com.example.project_spring.controller;

import com.example.project_spring.dto.FriendRequestDTO;
import com.example.project_spring.dto.UserDTO;
import com.example.project_spring.entity.User;
import com.example.project_spring.service.impl.FriendRequestServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api/requests")
public class FriendRequestController {

    private FriendRequestServiceImpl friendRequestService;

    @RequestMapping("/send")
    public FriendRequestDTO sendRequest(@RequestParam Long senderId, @RequestParam Long receiverId) {
        return friendRequestService.sendFriendRequest(senderId, receiverId);
    }

    @GetMapping("/received/{userId}")
    public List<FriendRequestDTO> getReceivedRequests(@PathVariable Long userId) {
        List<FriendRequestDTO> requests = friendRequestService.getReceivedRequests(userId);
        // Log the requests to verify the data

        return requests;
    }


    @PutMapping("/accept/{requestId}")
    public FriendRequestDTO acceptRequest(@PathVariable Long requestId) {
        return friendRequestService.acceptFriendRequest(requestId);
    }

    @DeleteMapping("/decline/{requestId}")
    public void declineRequest(@PathVariable Long requestId) {
        friendRequestService.declineFriendRequest(requestId);
    }

    @GetMapping("/friends/{userId}")
    public List<UserDTO> getFriends(@PathVariable Long userId) {
        List<User> friends = friendRequestService.getFriends(userId);  // Service method to fetch friends
        return friends.stream()
                .map(UserDTO::new)  // Assuming you have a UserDTO constructor to map to DTO
                .collect(Collectors.toList());
    }

}
