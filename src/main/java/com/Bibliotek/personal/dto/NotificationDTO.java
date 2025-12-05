package com.bibliotek.personal.dto;

import java.time.LocalDateTime;

public class NotificationDTO {
    private int id;
    private String type;
    private String title;
    private String message;
    private boolean read;
    private String relatedEmail;
    private Integer relatedBookId;
    private Integer relatedId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public NotificationDTO() {
    }

    public NotificationDTO(int id, String type, String title, String message, boolean read,
                          String relatedEmail, Integer relatedBookId, Integer relatedId,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.message = message;
        this.read = read;
        this.relatedEmail = relatedEmail;
        this.relatedBookId = relatedBookId;
        this.relatedId = relatedId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getRelatedEmail() {
        return relatedEmail;
    }

    public void setRelatedEmail(String relatedEmail) {
        this.relatedEmail = relatedEmail;
    }

    public Integer getRelatedBookId() {
        return relatedBookId;
    }

    public void setRelatedBookId(Integer relatedBookId) {
        this.relatedBookId = relatedBookId;
    }

    public Integer getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(Integer relatedId) {
        this.relatedId = relatedId;
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





