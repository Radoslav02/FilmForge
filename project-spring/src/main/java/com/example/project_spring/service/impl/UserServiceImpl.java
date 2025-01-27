package com.example.project_spring.service.impl;


import com.example.project_spring.dto.MovieDTO;
import com.example.project_spring.dto.UserDTO;

import com.example.project_spring.entity.Movie;
import com.example.project_spring.entity.User;
import com.example.project_spring.exception.ResourceNotFoundException;

import com.example.project_spring.mapper.UserMapper;

import com.example.project_spring.repository.MovieRepository;
import com.example.project_spring.repository.UserRepository;
import com.example.project_spring.service.UserService;
import lombok.AllArgsConstructor;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private final MovieRepository movieRepository;
    private JavaMailSender javaMailSender;

    public UserDTO createUser(UserDTO userDTO) {
        if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = UserMapper.mapToUser(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Enkodiranje
        user.setConfirmationPassword(passwordEncoder.encode(userDTO.getConfirmationPassword()));
        if (userDTO.getEmail().equals("radoslavpavkovg@gmail.com")){
            user.setRole("admin");
        }else {
            user.setRole("user");
        }
        user.setIsEnabled("");
        user.setVerificationToken(UUID.randomUUID().toString());

        User savedUser = userRepository.save(user);

        sendVerificationEmail(user.getEmail(), user.getVerificationToken());

        return UserMapper.mapToUserDTO(savedUser);
    }

    public void sendVerificationEmail(String toEmail, String token) {
        String subject = "Verify your email";
        String verificationLink = "http://localhost:8080/api/users/confirm?token=" + token;
        String body = "Thank you for registering! Please click the link below to verify your email:\n" + verificationLink;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        javaMailSender.send(message);
    }

    @Override
    public UserDTO getUserById(Long userID) {
        User user = userRepository.findById(userID).orElseThrow(() ->
                new ResourceNotFoundException("User with given id doesn't exist: " + userID));

        return UserMapper.mapToUserDTO(user);
    }


    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map((user) -> UserMapper.mapToUserDTO(user)).collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userID) {
        User user = userRepository.findById(userID).orElseThrow(() -> new ResourceNotFoundException("User with given id doesn't exist: " + userID));

        userRepository.deleteById(userID);
    }

    @Override
    public UserDTO authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResourceNotFoundException("Wrong password");
        }

        if (user.getIsEnabled().equals("")) {
            throw new RuntimeException("User account is not activated");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return UserMapper.mapToUserDTO(user);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + email));

        return UserMapper.mapToUserDTO(user);
    }

    @Override
    public UserDTO updateUserByEmail(String email, UserDTO updatedUser) {
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + email));

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setCity(updatedUser.getCity());
        existingUser.setCountry(updatedUser.getCountry());
        existingUser.setStreet(updatedUser.getStreet());
        existingUser.setNumber(updatedUser.getNumber());

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        User updatedUserObj = userRepository.save(existingUser);
        return UserMapper.mapToUserDTO(updatedUserObj);
    }

    public List<UserDTO> searchUsers(String username) {
        List<User> users = userRepository.findByUsernameContainingIgnoreCase(username);

        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }

        users.forEach(user -> System.out.println("Found user: " + user.getUsername()));

        return users.stream()
                .map(UserMapper::mapToUserDTO)
                .collect(Collectors.toList());
    }
}

