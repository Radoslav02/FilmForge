package com.example.project_spring.dto;

import com.example.project_spring.entity.Movie;
import com.example.project_spring.entity.FriendRequest;
import com.example.project_spring.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    private String verificationToken;
    private String isEnabled;
    
    @JsonIgnore
    private List<Movie> moviesAdded = new ArrayList<>();

    @JsonIgnore
    private List<FriendRequestDTO> sentRequests = new ArrayList<>();

    @JsonIgnore
    private List<FriendRequestDTO> receivedRequests = new ArrayList<>();

    public UserDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.city = user.getCity();
        this.street = user.getStreet();
        this.number = user.getNumber();
        this.username = user.getUsername();
        this.country = user.getCountry();
        this.role = user.getRole();
        this.registrationDate = user.getRegistrationDate();
        this.lastLogin = user.getLastLogin();
        this.verificationToken = user.getVerificationToken();
        this.isEnabled = user.getIsEnabled();
    }
}
