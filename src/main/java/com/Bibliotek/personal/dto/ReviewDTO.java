package com.bibliotek.personal.dto;

import com.bibliotek.personal.dto.user.UserDTO;

import java.time.LocalDateTime;

public class ReviewDTO {
    private int id;
    private int rating;
    private String comment;

    private UserDTO user;   // Convert User entity to UserDTO
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReviewDTO() {
    }

    private int bookId;  // Add this field to store the book ID

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }



    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
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
