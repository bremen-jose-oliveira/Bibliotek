package com.bibliotek.personal.dto.user;

import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*")
public class UserRegistrationDTO {
    private String email;
    private String username; // Optional for registration
    private String password; // Password for registration

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
