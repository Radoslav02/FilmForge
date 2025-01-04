package com.example.project_spring.repository;

import com.example.project_spring.dto.MovieDTO;
import com.example.project_spring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByLastLoginBefore(LocalDateTime dateTime);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByVerificationToken(String token);

    public List<User> findByUsernameContainingIgnoreCase(String username);


}
