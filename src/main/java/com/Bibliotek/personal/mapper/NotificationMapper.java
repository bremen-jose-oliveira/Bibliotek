package com.bibliotek.personal.mapper;

import com.bibliotek.personal.dto.NotificationDTO;
import com.bibliotek.personal.entity.Notification;

public class NotificationMapper {
    public static NotificationDTO toDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setType(notification.getType().name());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setRead(notification.isRead());
        dto.setRelatedId(notification.getRelatedId());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setUpdatedAt(notification.getUpdatedAt());

        if (notification.getRelatedUser() != null) {
            dto.setRelatedEmail(notification.getRelatedUser().getEmail());
        }

        if (notification.getRelatedBook() != null) {
            dto.setRelatedBookId(notification.getRelatedBook().getId());
        }

        return dto;
    }
}

