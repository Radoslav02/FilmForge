package com.example.project_spring.mapper;

import com.example.project_spring.dto.UserDTO;
import com.example.project_spring.entity.User;

public class UserMapper {
    public static UserDTO maptoUserDTO(User user) {
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
            user.getMoviesAdded()
        );
    }

    public static User maptoUser(UserDTO userDTO) {
        return new User(
                userDTO.getId(),
                userDTO.getFirstName(),
                userDTO.getLastName(),
                userDTO.getEmail(),
                userDTO.getPassword(),
                userDTO.getConfirmationPassword(),
                userDTO.getCity(),
                userDTO.getStreet(),
                userDTO.getNumber(),
                userDTO.getUsername(),
                userDTO.getCountry(),
                userDTO.getRole(),
                userDTO.getRegistrationDate(),
                userDTO.getLastLogin(),
                userDTO.getVerificationToken(),
                userDTO.isEnabled(),
                userDTO.getMoviesAdded()
        );
    }
}
