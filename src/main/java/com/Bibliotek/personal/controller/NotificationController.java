package com.bibliotek.personal.controller;

import com.bibliotek.personal.apiResponse.ApiResponse;
import com.bibliotek.personal.dto.NotificationDTO;
import com.bibliotek.personal.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(Authentication authentication) {
        String userEmail = authentication.getName();
        List<NotificationDTO> notifications = notificationService.getUserNotifications(userEmail);
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication authentication) {
        String userEmail = authentication.getName();
        long count = notificationService.getUnreadCount(userEmail);
        return new ResponseEntity<>(Map.of("count", count), HttpStatus.OK);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse> markAsRead(@PathVariable int id, Authentication authentication) {
        String userEmail = authentication.getName();
        notificationService.markAsRead(id, userEmail);
        return new ResponseEntity<>(new ApiResponse("Notification marked as read", true, 200), HttpStatus.OK);
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse> markAllAsRead(Authentication authentication) {
        String userEmail = authentication.getName();
        notificationService.markAllAsRead(userEmail);
        return new ResponseEntity<>(new ApiResponse("All notifications marked as read", true, 200), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteNotification(@PathVariable int id, Authentication authentication) {
        String userEmail = authentication.getName();
        notificationService.deleteNotification(id, userEmail);
        return new ResponseEntity<>(new ApiResponse("Notification deleted", true, 200), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteAllNotifications(Authentication authentication) {
        String userEmail = authentication.getName();
        notificationService.deleteAllNotifications(userEmail);
        return new ResponseEntity<>(new ApiResponse("All notifications deleted", true, 200), HttpStatus.OK);
    }
}





