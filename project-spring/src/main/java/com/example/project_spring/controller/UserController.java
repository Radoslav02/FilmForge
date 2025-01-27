package com.example.project_spring.controller;

import com.example.project_spring.dto.CategoryDTO;
import com.example.project_spring.dto.LoginRequestDTO;
import com.example.project_spring.dto.MovieDTO;
import com.example.project_spring.dto.UserDTO;
import com.example.project_spring.entity.User;
import com.example.project_spring.repository.UserRepository;
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

import java.util.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserServiceImpl userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MovieServiceImp movieServiceImp;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        try {

            System.out.println("Received UserDTO: " + userDTO.toString());
            UserDTO savedUser = userService.createUser(userDTO);

            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/confirm")
    public ResponseEntity<String> confirmEmail(@RequestParam("token") String token) {
        Optional<User> optionalUser = userRepository.findByVerificationToken(token);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setIsEnabled("activated");
            user.setVerificationToken(null); // Token više nije potreban
            userRepository.save(user);
            return ResponseEntity.ok("Email confirmed successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token.");
        }
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody LoginRequestDTO loginRequest) {
        System.out.println("Login request received: email=" + loginRequest.getEmail() + ", password=" + loginRequest.getPassword());

        try {
            UserDTO authenticatedUser = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());

            if (!authenticatedUser.getIsEnabled().equals("activated")) {
                System.err.println("Login failed: User account is disabled.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "User account is disabled."));
            }

            String token = jwtTokenProvider.createToken(authenticatedUser.getEmail(),authenticatedUser.getId(), List.of("USER"));

            System.out.println("Authentication successful for email: " + authenticatedUser.getEmail());

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
    public ResponseEntity<?> searchUsers(@RequestParam String username) {
        try {
            List<UserDTO> users = userService.searchUsers(username);

            if (users.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body(Collections.emptyList());
            }

            return ResponseEntity.ok(users);
        } catch (Exception e) {
            e.printStackTrace();  // Logovanje za debagovanje
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of(
                            "message", "An error occurred while searching for users.",
                            "error", e.getMessage()
                    )
            );
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
