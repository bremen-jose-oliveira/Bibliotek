package com.bibliotek.personal.controller;

import com.bibliotek.personal.apiResponse.ApiResponse;
import com.bibliotek.personal.dto.UserBookStatusDTO;
import com.bibliotek.personal.entity.UserBookStatus;
import com.bibliotek.personal.service.UserBookStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/user-book-status")
public class UserBookStatusController {

    private final UserBookStatusService userBookStatusService;

    @Autowired
    public UserBookStatusController(UserBookStatusService userBookStatusService) {
        this.userBookStatusService = userBookStatusService;
    }

    @PutMapping("/{userId}/{bookId}")
    public ResponseEntity<?> updateBookStatus(@PathVariable int userId, @PathVariable int bookId,
                                                           @RequestParam UserBookStatus.BookStatus status) {
        try {
            UserBookStatusDTO updatedStatus = userBookStatusService.updateBookStatus(userId, bookId, status);
            return new ResponseEntity<>(updatedStatus, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Service already handles DataIntegrityViolationException and wraps it in RuntimeException
            String message = e.getMessage();
            int statusCode = message != null && message.contains("not found") ? 404 : 400;
            return new ResponseEntity<>(new ApiResponse(message, false, statusCode), 
                statusCode == 404 ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("An error occurred: " + e.getMessage(), false, 500), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{userId}/{bookId}")
    public ResponseEntity<UserBookStatusDTO> getBookStatus(@PathVariable int userId, @PathVariable int bookId) {
        Optional<UserBookStatusDTO> status = userBookStatusService.getBookStatusDTO(userId, bookId);
        return status.map(bookStatus -> new ResponseEntity<>(bookStatus, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/my")
    public ResponseEntity<List<UserBookStatusDTO>> getMyBookStatuses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        List<UserBookStatusDTO> statuses = userBookStatusService.getBookStatusesForLoggedInUser(userEmail);
        return new ResponseEntity<>(statuses, HttpStatus.OK);
    }
}
