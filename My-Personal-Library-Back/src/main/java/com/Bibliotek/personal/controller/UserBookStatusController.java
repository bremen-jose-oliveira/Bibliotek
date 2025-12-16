package com.Bibliotek.personal.controller;


import com.Bibliotek.personal.entity.UserBookStatus;
import com.Bibliotek.personal.service.UserBookStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user-book-status")
public class UserBookStatusController {

    private final UserBookStatusService userBookStatusService;

    @Autowired
    public UserBookStatusController(UserBookStatusService userBookStatusService) {
        this.userBookStatusService = userBookStatusService;
    }

    @PutMapping("/{userId}/{bookId}")
    public ResponseEntity<UserBookStatus> updateBookStatus(@PathVariable int userId, @PathVariable int bookId,
                                                           @RequestParam UserBookStatus.BookStatus status) {
        UserBookStatus updatedStatus = userBookStatusService.updateBookStatus(userId, bookId, status);
        return new ResponseEntity<>(updatedStatus, HttpStatus.OK);
    }

    @GetMapping("/{userId}/{bookId}")
    public ResponseEntity<UserBookStatus.BookStatus> getBookStatus(@PathVariable int userId, @PathVariable int bookId) {
        Optional<UserBookStatus.BookStatus> status = userBookStatusService.getBookStatus(userId, bookId);
        return status.map(bookStatus -> new ResponseEntity<>(bookStatus, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
