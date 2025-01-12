package com.example.project_spring.repository;

import com.example.project_spring.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
    List<Message> findByReceiverId(Long receiverId);
}
