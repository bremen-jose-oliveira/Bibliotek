package com.bibliotek.personal.dto;





import java.time.LocalDateTime;



import java.time.LocalDateTime;

public class FriendshipDTO {
    private int id;
    private String userEmail;
    private String friendEmail;
    private String friendshipStatus;  // ✅ Add this field as a String
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ✅ Constructor
    public FriendshipDTO(int id, String userEmail, String friendEmail, String friendshipStatus, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userEmail = userEmail;
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
