package com.Bibliotek.personal.controller;


import com.Bibliotek.personal.dto.UserBookStatusDTO;
import com.Bibliotek.personal.entity.User;
import com.Bibliotek.personal.entity.UserBookStatus;
import com.Bibliotek.personal.mapper.UserBookStatusMapper;
import com.Bibliotek.personal.service.UserBookStatusService;
import com.Bibliotek.personal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user-book-status")
@CrossOrigin(origins = "*")
public class UserBookStatusController {

    private final UserBookStatusService userBookStatusService;
    private final UserService userService;

    @Autowired
    public UserBookStatusController(UserBookStatusService userBookStatusService, UserService userService) {
        this.userBookStatusService = userBookStatusService;
        this.userService = userService;
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
    
    @GetMapping("/my")
    public ResponseEntity<List<UserBookStatusDTO>> getMyReadingStatuses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        User user = userService.findByEmailIgnoreCase(email);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        List<UserBookStatus> statuses = userBookStatusService.getAllStatusesForUser(user.getId());
        List<UserBookStatusDTO> statusDTOs = statuses.stream()
                .map(UserBookStatusMapper::toDTO)
                .collect(Collectors.toList());
        
        return new ResponseEntity<>(statusDTOs, HttpStatus.OK);
    }
}
