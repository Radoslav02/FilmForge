package com.example.project_spring.service.impl;

import com.example.project_spring.dto.CategoryDTO;
import com.example.project_spring.dto.LoginRequestDTO;
import com.example.project_spring.dto.UserDTO;
import com.example.project_spring.entity.Category;
import com.example.project_spring.entity.User;
import com.example.project_spring.exception.ResourceNotFoundException;
import com.example.project_spring.mapper.CategoryMapper;
import com.example.project_spring.mapper.UserMapper;
import com.example.project_spring.repository.CategoryRepository;
import com.example.project_spring.repository.UserRepository;
import com.example.project_spring.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user = UserMapper.maptoUser(userDTO);
        LocalDateTime now = LocalDateTime.now();

        // Generate verification token
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setEnabled(false);  // User is not activated until email verification

        user.setRole("user");
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setConfirmationPassword(passwordEncoder.encode(userDTO.getConfirmationPassword()));
        user.setRegistrationDate(now);

        // Save the user to the database
        User savedUser = userRepository.save(user);

        // Convert saved user back to DTO
        UserDTO savedUserDTO = UserMapper.maptoUserDTO(savedUser);

        // Set the verification token in the DTO to send it back to the frontend
        savedUserDTO.setVerificationToken(token);  // Add token to DTO

        return savedUserDTO;
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

