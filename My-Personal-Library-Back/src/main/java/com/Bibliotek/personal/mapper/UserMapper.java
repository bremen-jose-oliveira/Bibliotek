package com.Bibliotek.personal.mapper;


import com.Bibliotek.personal.dto.user.UserDTO;
import com.Bibliotek.personal.entity.User;

public class UserMapper {
    // Convert UserDTO to User (Entity)
    public static User toEntity(UserDTO userDTO) {
        User user = new User();
        user.setEmail(userDTO.getEmail());


        // Only set username if it's provided
        if (userDTO.getUsername() != null && !userDTO.getUsername().isEmpty()) {
            user.setUsername(userDTO.getUsername());
        }

        // Password is handled in the service layer.
        return user;
    }

    // Convert User (Entity) to UserDTO
    public static UserDTO toDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());

        // Don't expose password in DTO
        userDTO.setUsername(user.getUsername());

        return userDTO;
    }
}