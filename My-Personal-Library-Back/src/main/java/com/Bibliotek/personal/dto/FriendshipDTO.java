package com.Bibliotek.personal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FriendshipDTO {
    private int id;
    private String userEmail;
    private String username;
    private String friendEmail;
    private String friendshipStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FriendshipDTO(int id, String userEmail, String username, String friendEmail, String friendshipStatus, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userEmail = userEmail;
        this.username = username;
        this.friendEmail = friendEmail;
        this.friendshipStatus = friendshipStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public FriendshipDTO(int id, String username, String friendEmail, String friendshipStatus, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.friendEmail = friendEmail;
        this.friendshipStatus = friendshipStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public FriendshipDTO() {

    }

    public String getFriendshipStatus() {
        return friendshipStatus;
    }


    public void setFriendshipStatus(String friendshipStatus) {
        this.friendshipStatus = friendshipStatus;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getFriendEmail() {
        return friendEmail;
    }

    public void setFriendEmail(String friendEmail) {
        this.friendEmail = friendEmail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
