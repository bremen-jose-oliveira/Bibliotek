package com.bibliotek.personal.mapper;

import com.bibliotek.personal.dto.UserBookStatusDTO;
import com.bibliotek.personal.entity.UserBookStatus;

public class UserBookStatusMapper {

    // Convert UserBookStatus entity to UserBookStatusDTO
    public static UserBookStatusDTO toDTO(UserBookStatus status) {
        UserBookStatusDTO dto = new UserBookStatusDTO();
        dto.setId(status.getId());
        dto.setBook(BookMapper.toDTO(status.getBook()));  // Convert book to BookDTO
        dto.setUser(UserMapper.toDTO(status.getUser()));  // Convert user to UserDTO
        dto.setStatus(status.getStatus().toString());  // Convert status to string
        dto.setVisibility(status.getVisibility().toString());  // Convert visibility to string
        dto.setCreatedAt(status.getCreatedAt());
        dto.setUpdatedAt(status.getUpdatedAt());
        return dto;
    }
}