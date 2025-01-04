package com.example.project_spring.dto;

import com.example.project_spring.entity.Movie;
import com.example.project_spring.entity.FriendRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String confirmationPassword;
    private String city;
    private String street;
    private String number;
    private String username;
    private String country;
    private String role;
    private LocalDateTime registrationDate;
    private LocalDateTime lastLogin;
    // E-mail verification
    private String verificationToken;
    private boolean isEnabled;
    private List<Movie> moviesAdded = new ArrayList<>();

    // Added sent and received friend requests
    private List<FriendRequestDTO> sentRequests = new ArrayList<>();
    private List<FriendRequestDTO> receivedRequests = new ArrayList<>();
}
