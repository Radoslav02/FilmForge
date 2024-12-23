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
            user.getRole(),
            user.getRegistrationDate(),
            user.getLastLogin(),
            user.getUsername()
        );
    }

    public static User maptoUser(UserDTO userDTO) {
        return new User(
                userDTO.getId(),
                userDTO.getFirstName(),
                userDTO.getLastName(),
                userDTO.getEmail(),
                userDTO.getPassword(),
                userDTO.getRole(),
                userDTO.getRegistrationDate(),
                userDTO.getLastLogin(),
                userDTO.getUsername()
        );
    }
}
