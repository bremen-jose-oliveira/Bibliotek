package com.bibliotek.personal.service;

import com.bibliotek.personal.dto.user.UserDTO;
import com.bibliotek.personal.dto.user.UserRegistrationDTO;
import com.bibliotek.personal.entity.PasswordResetToken;
import com.bibliotek.personal.entity.User;
import com.bibliotek.personal.mapper.UserMapper;
import com.bibliotek.personal.repository.PasswordResetTokenRepository;
import com.bibliotek.personal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final String MAILGUN_DOMAIN = "sandboxxxxx.mailgun.org";
    private static final String API_KEY = "your-mailgun-api-key";
    private final UserRepository userRepository;



    @Value("${FrontEnd.url}")
    private String FrontEndUrl;

    @Value("${Smtp.email}")
    private String smtpEmail;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDTO)
                .toList();
    }


    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserDTO getUserById(int id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(UserMapper::toDTO).orElse(null);
    }
    public List<UserDTO> getUsersByEmailOrUsername(String search) {
        // Call the updated repository query method
        List<User> users = userRepository.findByUsernameOrEmailContaining(search);

        // Convert User entities to UserDTOs
        return users.stream().map(UserMapper::toDTO).collect(Collectors.toList());
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

    public boolean sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) return false; // User not found



        // Generate reset token
        String token = UUID.randomUUID().toString();


        PasswordResetToken resetToken = new PasswordResetToken(user, token);

        tokenRepository.save(resetToken);


        String resetLink = FrontEndUrl+"/reset-password?token=" + token;

        String subject = "Reset Your Password";
        String body = "Click here to reset your password: " + resetLink;

        return sendEmail(email, subject, body);
    }

    private boolean sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(smtpEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
    public PasswordResetToken validatePasswordResetToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public void updateUserPassword(User user, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }




}
