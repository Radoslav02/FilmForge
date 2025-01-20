package com.example.project_spring.controller;

import com.example.project_spring.dto.FriendRequestDTO;
import com.example.project_spring.dto.UserDTO;
import com.example.project_spring.entity.User;
import com.example.project_spring.exception.ResourceNotFoundException;
import com.example.project_spring.service.impl.FriendRequestServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        List<User> friends = friendRequestService.getFriends(userId);
        return friends.stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }



    @DeleteMapping("/delete/{requestId}")
    public void deleteFriendRequest(@PathVariable Long requestId) {
        friendRequestService.deleteFriendRequest(requestId);
    }

    @GetMapping("/usersFriends/{userId}")
    public ResponseEntity<List<FriendRequestDTO>> getUsersFriends(@PathVariable Long userId) {
        try {
            System.out.println("Received userId: " + userId); // Log the received userId
            List<FriendRequestDTO> friends = friendRequestService.getUsersFriends(userId);
            return ResponseEntity.ok(friends);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}
