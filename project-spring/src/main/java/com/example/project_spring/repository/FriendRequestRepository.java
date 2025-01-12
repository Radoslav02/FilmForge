package com.example.project_spring.repository;

import com.example.project_spring.entity.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findByReceiverIdAndAccepted(Long receiverId, boolean accepted);
    List<FriendRequest> findBySenderIdAndAccepted(Long senderId, boolean accepted);
    List<FriendRequest> findByReceiverId(Long receiverId);


}
