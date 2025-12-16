package com.Bibliotek.personal.mapper;


import com.Bibliotek.personal.dto.ReviewDTO;
import com.Bibliotek.personal.dto.user.UserDTO;
import com.Bibliotek.personal.entity.Review;

public class ReviewMapper {

    public static ReviewDTO toDTO(Review review) {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setId(review.getId());
        reviewDTO.setRating(review.getRating());
        reviewDTO.setComment(review.getComment());
        reviewDTO.setCreatedAt(review.getCreatedAt());
        reviewDTO.setUpdatedAt(review.getUpdatedAt());

        // Set only the book ID instead of embedding full BookDTO
        reviewDTO.setBookId(review.getBook().getId());

        // Convert User entity to UserDTO (excluding unnecessary fields)
        UserDTO userDTO = new UserDTO();
        userDTO.setId(review.getUser().getId());
        userDTO.setUsername(review.getUser().getUsername());
        userDTO.setEmail(review.getUser().getEmail());
        reviewDTO.setUser(userDTO);

        return reviewDTO;
    }


}
