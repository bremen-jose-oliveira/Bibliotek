package com.bibliotek.personal.controller;

import com.bibliotek.personal.entity.PasswordResetToken;
import com.bibliotek.personal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("Email is required");
            }
            userService.sendPasswordResetEmail(email);
            return ResponseEntity.ok(Collections.singletonMap("message", "Reset email sent!"));
        } catch (Exception e) {
            e.printStackTrace(); // Debugging
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("password");

        // Validate Token
        PasswordResetToken resetToken = userService.validatePasswordResetToken(token);
        if (resetToken == null || resetToken.isExpired()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", "Invalid or expired token"));
        }

        // Update Password
        userService.updateUserPassword(resetToken.getUser(), newPassword);
        return ResponseEntity.ok(Collections.singletonMap("message", "Password updated successfully!"));
    }
}