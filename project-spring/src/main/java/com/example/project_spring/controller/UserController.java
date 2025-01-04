package com.example.project_spring.controller;

import com.example.project_spring.dto.CategoryDTO;
import com.example.project_spring.dto.LoginRequestDTO;
import com.example.project_spring.dto.MovieDTO;
import com.example.project_spring.dto.UserDTO;
import com.example.project_spring.security.jwt.JwtTokenProvider;
import com.example.project_spring.service.impl.CategoryServiceImpl;
import com.example.project_spring.service.impl.MovieServiceImp;
import com.example.project_spring.service.impl.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserServiceImpl userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MovieServiceImp movieServiceImp;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO userDTO) {
        UserDTO savedUser = userService.createUser(userDTO);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or missing token.");
        }

        boolean isVerified = userService.verifyUserEmail(token);
        if (isVerified) {
            return ResponseEntity.ok("Email successfully verified.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody LoginRequestDTO loginRequest) {
        // Log ulaznih podataka
        System.out.println("Login request received: email=" + loginRequest.getEmail() + ", password=" + loginRequest.getPassword());

        try {
            // Autentifikacija korisnika
            UserDTO authenticatedUser = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());

            // Provera da li je korisnik enabled
            if (!authenticatedUser.isEnabled()) {
                System.err.println("Login failed: User account is disabled.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "User account is disabled."));
            }

            // Generisanje tokena za autentifikovanog korisnika
            String token = jwtTokenProvider.createToken(authenticatedUser.getEmail(), List.of("USER"));

            // Log uspešne autentifikacije
            System.out.println("Authentication successful for email: " + authenticatedUser.getEmail());

            // Formiranje odgovora
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("email", authenticatedUser.getEmail());
            response.put("firstName", authenticatedUser.getFirstName());
            response.put("lastName", authenticatedUser.getLastName());
            response.put("username", authenticatedUser.getUsername());
            response.put("city", authenticatedUser.getCity());
            response.put("country", authenticatedUser.getCountry());
            response.put("street", authenticatedUser.getStreet());
            response.put("number", authenticatedUser.getNumber());
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

            System.out.println("Received user update payload: " + updatedUser);

            String email = authentication.getName();
            UserDTO updatedUserProfile = userService.updateUserByEmail(email, updatedUser);


            System.out.println("Successfully updated user profile: " + updatedUserProfile);

            return ResponseEntity.ok(updatedUserProfile);
        } catch (RuntimeException e) {

            System.err.println("Error updating profile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String username) {
        try {
            List<UserDTO> users = userService.searchUsers(username);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ArrayList<>());  // Ensure the response is always valid
        }
    }

    @GetMapping("/{id}/movies")
    public ResponseEntity<List<MovieDTO>> getUserMovies(@PathVariable Long id) {
        try {
            List<MovieDTO> movies = movieServiceImp.getMoviesByUserId(id);
            return ResponseEntity.ok(movies);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null); // If user is not found, return 404
        }
    }

    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }
}
