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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user = UserMapper.maptoUser(userDTO);

        // Šifrujte lozinku pre čuvanja
        user.setRole("user");
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        User savedUser = userRepository.save(user);
        return UserMapper.maptoUserDTO(savedUser);
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
    public UserDTO updateUser(Long userID, UserDTO updatedUser) {
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new ResourceNotFoundException("User with given id doesn't exist: " + userID));

        user.setEmail(updatedUser.getEmail());
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());

        // Only update the password if it is not null or empty
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(updatedUser.getPassword());
        }

        User updatedUserObj = userRepository.save(user);

        return UserMapper.maptoUserDTO(updatedUserObj);
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


    // Zakazano svakog dana u ponoć
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteInactiveUsers() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<User> inactiveUsers = userRepository.findByLastLoginBefore(sevenDaysAgo);

        for (User user : inactiveUsers) {
            userRepository.delete(user);
        }
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

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }


}
