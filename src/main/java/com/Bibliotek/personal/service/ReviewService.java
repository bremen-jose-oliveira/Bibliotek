package com.bibliotek.personal.service;

import com.bibliotek.personal.dto.ReviewDTO;
import com.bibliotek.personal.dto.ReviewRequestDTO;
import com.bibliotek.personal.entity.Book;
import com.bibliotek.personal.entity.Notification;
import com.bibliotek.personal.entity.Review;
import com.bibliotek.personal.entity.User;
import com.bibliotek.personal.exception.ResourceNotFoundException;
import com.bibliotek.personal.mapper.ReviewMapper;
import com.bibliotek.personal.repository.BookRepository;
import com.bibliotek.personal.repository.ReviewRepository;
import com.bibliotek.personal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, 
                        BookRepository bookRepository, 
                        UserRepository userRepository,
                        NotificationService notificationService) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public ReviewDTO createReview(ReviewRequestDTO reviewRequest, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with email: " + userEmail);
        }

        Book book = bookRepository.findById(reviewRequest.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + reviewRequest.getBookId()));

        // Check if user already reviewed this book
        Optional<Review> existingReview = reviewRepository.findByBookAndUser(book, user);
        if (existingReview.isPresent()) {
            throw new RuntimeException("User has already reviewed this book");
        }

        Review review = new Review();
        review.setBook(book);
        review.setUser(user);
        review.setRating(reviewRequest.getRating());
        review.setComment(reviewRequest.getComment());

        Review savedReview = reviewRepository.save(review);
        
        // Create notification for book owner (if not the reviewer)
        if (book.getOwner() != null && !book.getOwner().getEmail().equals(userEmail)) {
            notificationService.createNotification(
                book.getOwner().getEmail(),
                Notification.NotificationType.REVIEW_ADDED,
                "New Review",
                user.getUsername() + " reviewed \"" + book.getBookDetails().getTitle() + "\"",
                userEmail,
                book.getId(),
                savedReview.getId()
            );
        }
        
        return ReviewMapper.toDTO(savedReview);
    }

    public ReviewDTO updateReview(int reviewId, ReviewRequestDTO reviewRequest, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with email: " + userEmail);
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));

        // Ensure only the review author can update it
        if (review.getUser().getId() != user.getId()) {
            throw new RuntimeException("Unauthorized: You are not the author of this review.");
        }

        review.setRating(reviewRequest.getRating());
        review.setComment(reviewRequest.getComment());

        Review updatedReview = reviewRepository.save(review);
        return ReviewMapper.toDTO(updatedReview);
    }

    public boolean deleteReview(int reviewId, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with email: " + userEmail);
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));

        // Ensure only the review author can delete it
        if (review.getUser().getId() != user.getId()) {
            throw new RuntimeException("Unauthorized: You are not the author of this review.");
        }

        reviewRepository.delete(review);
        return true;
    }

    public List<ReviewDTO> getReviewsByBook(int bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + bookId));

        return reviewRepository.findReviewsByBook(book).stream()
                .map(ReviewMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ReviewDTO> getReviewsByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with email: " + userEmail);
        }

        return reviewRepository.findByUser(user).stream()
                .map(ReviewMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ReviewDTO getReviewById(int reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));
        return ReviewMapper.toDTO(review);
    }
}

