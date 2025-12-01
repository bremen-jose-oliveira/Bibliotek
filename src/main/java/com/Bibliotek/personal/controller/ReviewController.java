package com.bibliotek.personal.controller;

import com.bibliotek.personal.apiResponse.ApiResponse;
import com.bibliotek.personal.dto.ReviewDTO;
import com.bibliotek.personal.dto.ReviewRequestDTO;
import com.bibliotek.personal.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewRequestDTO reviewRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        ReviewDTO createdReview = reviewService.createReview(reviewRequest, currentUserEmail);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable int id, @RequestBody ReviewRequestDTO reviewRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        ReviewDTO updatedReview = reviewService.updateReview(id, reviewRequest, currentUserEmail);
        return new ResponseEntity<>(updatedReview, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteReview(@PathVariable int id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        boolean success = reviewService.deleteReview(id, currentUserEmail);
        if (success) {
            return new ResponseEntity<>(new ApiResponse("Review deleted successfully", true, 200), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("Review not found", false, 404), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByBook(@PathVariable int bookId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByBook(bookId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReviewDTO>> getMyReviews() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        List<ReviewDTO> reviews = reviewService.getReviewsByUser(currentUserEmail);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable int id) {
        ReviewDTO review = reviewService.getReviewById(id);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }
}

