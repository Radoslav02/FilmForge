package com.example.project_spring.mapper;

import com.example.project_spring.dto.FriendRequestDTO;
import com.example.project_spring.dto.UserDTO;
import com.example.project_spring.entity.FriendRequest;
import com.example.project_spring.entity.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserDTO mapToUserDTO(User user) {
        if (user == null) {
            return null;
        }

        // Mapiranje liste poslatih zahteva
        List<FriendRequestDTO> sentRequests = user.getSentRequests() != null
                ? user.getSentRequests().stream()
                .map(FriendRequestMapper::toDTO)
                .collect(Collectors.toList())
                : null;

        // Mapiranje liste primljenih zahteva
        List<FriendRequestDTO> receivedRequests = user.getReceivedRequests() != null
                ? user.getReceivedRequests().stream()
                .map(FriendRequestMapper::toDTO)
                .collect(Collectors.toList())
                : null;

        // Kreiraj i vrati UserDTO
        return new UserDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                user.getConfirmationPassword(),
                user.getCity(),
                user.getStreet(),
                user.getNumber(),
                user.getUsername(),
                user.getCountry(),
                user.getRole(),
                user.getRegistrationDate(),
                user.getLastLogin(),
                user.getVerificationToken(),
                user.isEnabled(),
                user.getMoviesAdded(),
                sentRequests,
                receivedRequests
        );
    }

    public static User mapToUser(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        // Mapiranje User-a bez FriendRequest entiteta
        User user = new User();
        user.setId(userDTO.getId());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setConfirmationPassword(userDTO.getConfirmationPassword());
        user.setCity(userDTO.getCity());
        user.setStreet(userDTO.getStreet());
        user.setNumber(userDTO.getNumber());
        user.setUsername(userDTO.getUsername());
        user.setCountry(userDTO.getCountry());
        user.setRole(userDTO.getRole());
        user.setRegistrationDate(userDTO.getRegistrationDate());
        user.setLastLogin(userDTO.getLastLogin());
        user.setVerificationToken(userDTO.getVerificationToken());
        user.setEnabled(userDTO.isEnabled());
        user.setMoviesAdded(userDTO.getMoviesAdded());

        // Prilagodba za zahteve se obično radi negde drugde u servisu, jer entitet zahteva može zavisiti od dodatnih podataka

        return user;
    }
}
