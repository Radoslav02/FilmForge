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
    private String role;
    private Date registrationDate;
    @Column(name = "last_login")
    private LocalDateTime lastLogin;  // Dodaj ovo polje za praćenje poslednje prijave
}