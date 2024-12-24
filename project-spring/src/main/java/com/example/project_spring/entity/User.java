package com.example.project_spring.entity;

import lombok.*;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    //E-mail verification
    private String verificationToken;
    private boolean isEnabled;
}