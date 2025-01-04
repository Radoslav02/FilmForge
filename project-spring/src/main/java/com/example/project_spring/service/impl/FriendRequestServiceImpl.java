package com.example.project_spring.service.impl;

import com.example.project_spring.dto.FriendRequestDTO;
import com.example.project_spring.entity.FriendRequest;
import com.example.project_spring.entity.User;
import com.example.project_spring.mapper.FriendRequestMapper;
import com.example.project_spring.repository.FriendRequestRepository;
import com.example.project_spring.repository.UserRepository;
import com.example.project_spring.service.FriendRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendRequestServiceImpl implements FriendRequestService {

    private final UserRepository userRepository;
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    public FriendRequestServiceImpl(UserRepository userRepository, FriendRequestRepository friendRequestRepository) {
        this.userRepository = userRepository;
        this.friendRequestRepository = friendRequestRepository;
    }

    public FriendRequestServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public FriendRequestDTO sendFriendRequest(Long senderId, Long receiverId) {
        try {
            User sender = userRepository.findById(senderId)
                    .orElseThrow(() -> new RuntimeException("Sender not found"));

            User receiver = userRepository.findById(receiverId)
                    .orElseThrow(() -> new RuntimeException("Receiver not found"));

            FriendRequest request = new FriendRequest(sender, receiver);
            friendRequestRepository.save(request);

            return new FriendRequestDTO(request);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Database integrity issue: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to send friend request: " + e.getMessage());
        }
    }

    public List<FriendRequestDTO> getRequestsForUser(Long userId) {
        return friendRequestRepository.findByReceiverId(userId)
                .stream()
                .map(FriendRequestMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendRequestDTO> getReceivedRequests(Long userId) {
        return friendRequestRepository.findByReceiverId(userId)
                .stream()
                .map(FriendRequestMapper::toDTO)
                .collect(Collectors.toList());
    }

    public FriendRequestDTO acceptFriendRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setAccepted(true);
        friendRequestRepository.save(request);
        return new FriendRequestDTO(request);
    }

    public void declineFriendRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        friendRequestRepository.delete(request);
    }


}
