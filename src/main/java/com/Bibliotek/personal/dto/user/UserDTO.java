package com.bibliotek.personal.dto.user;

import com.bibliotek.personal.entity.Friendship;

import java.util.List;

public class UserDTO {

    private int id;
    private String username;
    private String email;

    public UserDTO() {
    }



    public UserDTO(String email, String username) {
        this.email = email;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
