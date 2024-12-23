package com.example.project_spring.controller;

import com.example.project_spring.dto.CategoryDTO;
import com.example.project_spring.dto.LoginRequestDTO;
import com.example.project_spring.dto.UserDTO;
import com.example.project_spring.security.jwt.JwtTokenProvider;
import com.example.project_spring.service.impl.CategoryServiceImpl;
import com.example.project_spring.service.impl.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserServiceImpl userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO userDTO) {
        UserDTO savedUser = userService.createUser(userDTO);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody LoginRequestDTO loginRequest) {
        // Log ulaznih podataka
        System.out.println("Login request received: email=" + loginRequest.getEmail() + ", password=" + loginRequest.getPassword());

        try {
            UserDTO authenticatedUser = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
            String token = jwtTokenProvider.createToken(authenticatedUser.getEmail(), List.of("USER"));

            // Log uspešne autentifikacije
            System.out.println("Authentication successful for email: " + authenticatedUser.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("email", authenticatedUser.getEmail());
            response.put("firstName", authenticatedUser.getFirstName());
            response.put("lastName", authenticatedUser.getLastName());
            response.put("id", authenticatedUser.getId());
            response.put("isAdmin", authenticatedUser.getRole());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Log greške
            System.err.println("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }



    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getUserProfile(Authentication authentication) {
        String email = authentication.getName();
        UserDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/editProfile")
    public ResponseEntity<UserDTO> updateUserProfile(@RequestBody UserDTO updatedUser, Authentication authentication) {
        try {
            // Log podataka koje je frontend poslao
            System.out.println("Received user update payload: " + updatedUser);

            String email = authentication.getName(); // Dohvati email iz JWT tokena
            UserDTO updatedUserProfile = userService.updateUserByEmail(email, updatedUser);

            // Log uspešne izmene
            System.out.println("Successfully updated user profile: " + updatedUserProfile);

            return ResponseEntity.ok(updatedUserProfile);
        } catch (RuntimeException e) {
            // Log greške
            System.err.println("Error updating profile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


}
