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
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Override
    public UserDTO createUser(UserDTO userDTO) {

        User user = UserMapper.maptoUser(userDTO);
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
        List<User> users = userRepository.findAll();
        User wantedUser = null;

        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                wantedUser = user;
                break;
            }
        }

        if (wantedUser == null) {
            throw new RuntimeException("Invalid email or password");
        }

        // Ažuriraj poslednju prijavu korisnika
        wantedUser.setLastLogin(LocalDateTime.now());
        userRepository.save(wantedUser);

        return UserMapper.maptoUserDTO(wantedUser);
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
}
