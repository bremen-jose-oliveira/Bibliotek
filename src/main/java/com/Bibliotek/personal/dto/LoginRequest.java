package com.bibliotek.personal.dto;

import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*")
public class LoginRequest {
    private String email;
    private String password; // Password for login

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
