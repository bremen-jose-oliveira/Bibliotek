package com.Bibliotek.personal.controller;


import com.Bibliotek.personal.apiResponse.ApiResponse;
import com.Bibliotek.personal.dto.FriendshipDTO;
import com.Bibliotek.personal.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/friendships")
public class FriendshipController {

    private final FriendshipService friendshipService;

    @Autowired
    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @GetMapping("/requests")
    public ResponseEntity<List<FriendshipDTO>> getIncomingFriendRequests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        List<FriendshipDTO> requests = friendshipService.getIncomingFriendRequests(currentUsername);
        return ResponseEntity.ok(requests);
    }


    @PostMapping("/request")
    public ResponseEntity<ApiResponse> sendFriendRequest(@RequestBody Map<String, String> request) {
        String friendEmail = request.get("friendEmail");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        boolean success = friendshipService.sendFriendRequest(currentUsername, friendEmail);
        if (success) {
            return new ResponseEntity<>(new ApiResponse("Friend request sent successfully", true, 201), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new ApiResponse("Failed to send friend request", false, 400), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/approve")
    public ResponseEntity<ApiResponse> approveFriendRequest(@RequestBody Map<String, String> request) {
        String friendEmail = request.get("friendEmail");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        boolean success = friendshipService.approveFriendRequest(currentUsername, friendEmail);
        if (success) {
            return new ResponseEntity<>(new ApiResponse("Friend request approved", true, 200), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("Failed to approve friend request", false, 400), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/reject")
    public ResponseEntity<ApiResponse> rejectFriendRequest(@RequestBody Map<String, String> request) {
        String friendEmail = request.get("friendEmail");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        boolean success = friendshipService.rejectFriendRequest(currentUsername, friendEmail);
        if (success) {
            return new ResponseEntity<>(new ApiResponse("Friend request rejected", true, 200), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("Failed to reject friend request", false, 400), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping
    public ResponseEntity<List<FriendshipDTO>> getFriendsForCurrentUser() {
        List<FriendshipDTO> friends = friendshipService.getFriends();
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteFriend(@PathVariable Integer id) {
        boolean success = friendshipService.removeFriend(id);
        if (success) {
            return new ResponseEntity<>(new ApiResponse("Friend removed successfully", true, 200), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ApiResponse("Friend not found", false, 404), HttpStatus.NOT_FOUND);
        }
    }
}

