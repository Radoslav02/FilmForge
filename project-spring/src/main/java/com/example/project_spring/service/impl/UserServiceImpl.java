package com.example.project_spring.service.impl;


import com.example.project_spring.dto.UserDTO;

import com.example.project_spring.entity.User;
import com.example.project_spring.exception.ResourceNotFoundException;

import com.example.project_spring.mapper.UserMapper;

import com.example.project_spring.repository.UserRepository;
import com.example.project_spring.service.UserService;
import lombok.AllArgsConstructor;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = UserMapper.maptoUser(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEnabled(false);
        user.setVerificationToken(UUID.randomUUID().toString());
        User savedUser = userRepository.save(user);
        return UserMapper.maptoUserDTO(savedUser);
    }

    public boolean verifyUserEmail(String token) {
        Optional<User> user = userRepository.findByVerificationToken(token);
        if (user.isPresent()) {
            User verifiedUser = user.get();
            verifiedUser.setEnabled(true);
            verifiedUser.setVerificationToken(null);
            userRepository.save(verifiedUser);
            return true;
        }
        return false;
    }



    @Override
    public UserDTO getUserById(Long userID) {
        User user = userRepository.findById(userID).orElseThrow(() ->
                new ResourceNotFoundException("User with given id doesn't exist: " + userID));

        return UserMapper.maptoUserDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map((user) -> UserMapper.maptoUserDTO(user)).collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userID) {
        User user = userRepository.findById(userID).orElseThrow(() -> new ResourceNotFoundException("User with given id doesn't exist: " + userID));

        userRepository.deleteById(userID);
    }

    @Override
    public UserDTO authenticateUser(String email, String password) {
        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Validate password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResourceNotFoundException("Wrong password");
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("User account is not activated");
        }

        // Update the last login time
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Map user entity to DTO
        return UserMapper.maptoUserDTO(user);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + email));

        return UserMapper.maptoUserDTO(user);
    }

    @Override
    public UserDTO updateUserByEmail(String email, UserDTO updatedUser) {
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + email));

        // Ažuriraj osnovne podatke
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());

        // Ako postoji nova lozinka, šifrujte je pre čuvanja
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        User updatedUserObj = userRepository.save(existingUser);
        return UserMapper.maptoUserDTO(updatedUserObj);
    }
}

