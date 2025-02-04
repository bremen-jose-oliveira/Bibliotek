package com.bibliotek.personal.service;

import com.bibliotek.personal.dto.UserDTO;
import com.bibliotek.personal.dto.UserRegistrationDTO;
import com.bibliotek.personal.entity.User;
import com.bibliotek.personal.mapper.UserMapper;
import com.bibliotek.personal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDTO)
                .toList();
    }
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserDTO getUserById(int id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(UserMapper::toDTO).orElse(null);
    }

    public UserDTO createUser(UserRegistrationDTO userDTO) {
        // Check if user exists by email
        User existingUser = userRepository.findByEmail(userDTO.getEmail());
        if (existingUser != null) {
            throw new RuntimeException("User with this email already exists");
        }

        // Convert DTO to Entity
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setUsername(userDTO.getUsername());

        // Handle password (required for regular users)
        if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
            String defaultPassword = "OAuthGeneratedPassword123"; // Fallback for OAuth users
            user.setPassword(new BCryptPasswordEncoder().encode(defaultPassword));
        } else {
            user.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));
        }




        userRepository.save(user);


        return UserMapper.toDTO(user);
    }


    public UserDTO updateUser(int id, UserDTO userDTO) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = existingUser.get();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        userRepository.save(user);
        return UserMapper.toDTO(user);
    }

    public void deleteUser(int id) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        userRepository.delete(existingUser.get());
    }

    public UserDTO login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null || !new BCryptPasswordEncoder().matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        return UserMapper.toDTO(user);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
