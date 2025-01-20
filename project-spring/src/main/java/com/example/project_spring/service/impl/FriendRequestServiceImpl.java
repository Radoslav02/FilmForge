package com.example.project_spring.service.impl;

import com.example.project_spring.dto.FriendRequestDTO;
import com.example.project_spring.entity.FriendRequest;
import com.example.project_spring.entity.User;
import com.example.project_spring.exception.ResourceNotFoundException;
import com.example.project_spring.mapper.FriendRequestMapper;
import com.example.project_spring.repository.FriendRequestRepository;
import com.example.project_spring.repository.UserRepository;
import com.example.project_spring.service.FriendRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
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
        if (senderId.equals(receiverId)) {
            throw new RuntimeException("Korisnik ne može sam sebi poslati zahtev za prijateljstvo.");
        }

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

    @Override
    public List<User> getFriends(Long userId) {

        List<FriendRequest> receivedRequests = friendRequestRepository.findByReceiverIdAndAccepted(userId, true);


        List<FriendRequest> sentRequests = friendRequestRepository.findBySenderIdAndAccepted(userId, true);


        List<User> friends = receivedRequests.stream()
                .map(FriendRequest::getSender)
                .collect(Collectors.toList());

        // Add users who accepted the user's request
        sentRequests.stream()
                .map(FriendRequest::getReceiver)
                .forEach(friends::add);

        return friends;
    }

    public void deleteFriendRequest(Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));
        friendRequestRepository.delete(request);
    }

    public List<FriendRequestDTO> getUsersFriends(Long userId) {
        // Fetch all received and sent requests for the given user
        List<FriendRequest> receivedRequests = friendRequestRepository.findByReceiverId(userId);
        List<FriendRequest> sentRequests = friendRequestRepository.findBySenderId(userId);

        List<FriendRequestDTO> requestsDTO = new ArrayList<>();

        // Process received requests and check for accepted ones
        for (FriendRequest request : receivedRequests) {
            if (request.isAccepted()) {
                Long requestId = request.getId();
                Long senderId = request.getSender().getId();
                Long receiverId = request.getReceiver().getId();
                Date date = request.getRequestDate();
                String userName = request.getSender().getUsername();
                String name = request.getSender().getFirstName();
                String lastName = request.getSender().getLastName();
                Long friendId = request.getSender().getId();
                requestsDTO.add(new FriendRequestDTO(requestId, senderId, receiverId, date, userName, name, lastName, friendId));
            }
        }

        // Process sent requests and check for accepted ones
        for (FriendRequest request : sentRequests) {
            if (request.isAccepted()) {
                Long requestId = request.getId();
                Long senderId = request.getSender().getId();
                Long receiverId = request.getReceiver().getId();
                Date date = request.getRequestDate();
                String userName = request.getReceiver().getUsername();  // Receiver is the friend in this case
                String name = request.getReceiver().getFirstName();
                String lastName = request.getReceiver().getLastName();
                Long friendId = request.getReceiver().getId();
                requestsDTO.add(new FriendRequestDTO(requestId, senderId, receiverId, date, userName, name, lastName, friendId));
            }
        }

        return requestsDTO;
    }





}
