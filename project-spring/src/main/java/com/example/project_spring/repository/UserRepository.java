package com.example.project_spring.repository;

import com.example.project_spring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByLastLoginBefore(LocalDateTime dateTime);
}
