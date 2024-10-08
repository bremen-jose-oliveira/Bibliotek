package com.Bibliotek.Personal.controller;

import com.Bibliotek.Personal.dao.ReviewDAO;
import com.Bibliotek.Personal.entity.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews") // Base URL for review-related requests
public class ReviewController {

    private final ReviewDAO reviewDAO;

    @Autowired
    public ReviewController(ReviewDAO reviewDAO) {
        this.reviewDAO = reviewDAO;
    }

    @GetMapping // Get all reviews
    public List<Review> getAllReviews() {
        return reviewDAO.findAll();
    }

    @GetMapping("/{id}") // Get a review by ID
    public ResponseEntity<Review> getReviewById(@PathVariable Integer id) {
        Review review = reviewDAO.findById(id);
        return (review != null) ? new ResponseEntity<>(review, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping // Create a new review
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        reviewDAO.save(review);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }

    @PutMapping("/{id}") // Update an existing review
    public ResponseEntity<Review> updateReview(@PathVariable Integer id, @RequestBody Review review) {
        Review existingReview = reviewDAO.findById(id);
        if (existingReview == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        review.setId(id); // Ensure the ID is set for the update
        reviewDAO.save(review);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    @DeleteMapping("/{id}") // Delete a review
    public ResponseEntity<Void> deleteReview(@PathVariable Integer id) {
        Review existingReview = reviewDAO.findById(id);
        if (existingReview == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        reviewDAO.delete(existingReview);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
