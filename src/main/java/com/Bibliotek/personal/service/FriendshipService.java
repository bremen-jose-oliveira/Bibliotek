package com.bibliotek.personal.service;

import com.bibliotek.personal.dto.FriendshipDTO;
import com.bibliotek.personal.entity.Friendship;
import com.bibliotek.personal.entity.Notification;
import com.bibliotek.personal.entity.User;
import com.bibliotek.personal.repository.FriendshipRepository;
import com.bibliotek.personal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FriendshipService {
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final NotificationService notificationService;

    @Autowired
    public FriendshipService(UserRepository userRepository, 
                            FriendshipRepository friendshipRepository,
                            NotificationService notificationService) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.notificationService = notificationService;
    }

    public boolean sendFriendRequest(String userEmail, String friendEmail) {
        User user = userRepository.findByEmail(userEmail);
        User friend = userRepository.findByEmail(friendEmail);


        if (user == null || friend == null || user.equals(friend)) {
            return false;
        }


        Optional<Friendship> existingFriendship = friendshipRepository.findByUserAndFriend(user, friend);
        Optional<Friendship> reverseFriendship = friendshipRepository.findByUserAndFriend(friend, user);

        if (existingFriendship.isPresent() || reverseFriendship.isPresent()) {
            return false; // Friendship already exists
        }


        Friendship friendship = new Friendship();
        friendship.setUser(user);
        friendship.setFriend(friend);
        friendship.setStatus(Friendship.FriendshipStatus.PENDING);

        friendshipRepository.save(friendship);

        // Create notification for the friend
        notificationService.createNotification(
            friendEmail,
            Notification.NotificationType.FRIEND_REQUEST,
            "New Friend Request",
            user.getUsername() + " sent you a friend request",
            userEmail,
            null,
            friendship.getId()
        );

        return true;

    }


    public boolean approveFriendRequest(String userEmail, String friendEmail) {
        User user = userRepository.findByEmail(userEmail);
        User friend = userRepository.findByEmail(friendEmail);

        if (user == null || friend == null) {
            return false;
        }

        Optional<Friendship> friendshipOptional = friendshipRepository.findByUserAndFriend(friend, user);
        if (friendshipOptional.isPresent() && friendshipOptional.get().getStatus() == Friendship.FriendshipStatus.PENDING) {
            Friendship friendship = friendshipOptional.get();
            friendship.setStatus(Friendship.FriendshipStatus.ACCEPTED);
            friendshipRepository.save(friendship);
            
            // Create notification for the requester
            notificationService.createNotification(
                friendEmail,
                Notification.NotificationType.FRIEND_REQUEST_ACCEPTED,
                "Friend Request Accepted",
                user.getUsername() + " accepted your friend request",
                userEmail,
                null,
                friendship.getId()
            );
            
            return true;
        }
        return false;
    }

    public boolean rejectFriendRequest(String userEmail, String friendEmail) {
        User user = userRepository.findByEmail(userEmail);
        User friend = userRepository.findByEmail(friendEmail);

        if (user == null || friend == null) {
            return false;
        }

        Optional<Friendship> friendshipOptional = friendshipRepository.findByUserAndFriend(friend, user);
        if (friendshipOptional.isPresent() && friendshipOptional.get().getStatus() == Friendship.FriendshipStatus.PENDING) {
            friendshipRepository.delete(friendshipOptional.get());
            return true;
        }
        return false;
    }

    public List<FriendshipDTO> getFriends() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        User user = userRepository.findByEmail(currentUserEmail);
        if (user == null) {
            return List.of();
        }

        List<Friendship> friendships = friendshipRepository.findAllAcceptedFriendships(user);

        // Use a Set to track unique friend emails to avoid duplicates
        Set<String> seenEmails = new HashSet<>();
        
        return friendships.stream()
                .map(f -> {
                    // Determine which user is the friend (not the current user)
                    // Compare by ID to avoid issues with equals() implementation
                    User friendUser = f.getUser().getId() == user.getId() ? f.getFriend() : f.getUser();
                    return new FriendshipDTO(
                            f.getId(),
                            friendUser.getUsername(),
                            friendUser.getEmail(),
                            f.getStatus().name(),
                            f.getCreatedAt(),
                            f.getUpdatedAt());
                })
                .filter(dto -> {
                    // Filter out duplicates based on email
                    if (seenEmails.contains(dto.getFriendEmail())) {
                        return false;
                    }
                    seenEmails.add(dto.getFriendEmail());
                    return true;
                })
                .collect(Collectors.toList());
    }


    public boolean removeFriend(Integer id) {
        Optional<Friendship> friendshipOptional = friendshipRepository.findById(id);

        if (friendshipOptional.isPresent()) {
            friendshipRepository.delete(friendshipOptional.get());
            return true;
        }

        return false;
    }
    // In FriendshipService.java
    public List<FriendshipDTO> getIncomingFriendRequests(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            return List.of();
        }

        List<Friendship> incomingRequests = friendshipRepository.findAllByFriendAndStatus(user, Friendship.FriendshipStatus.PENDING);

        return incomingRequests.stream()
                .map(f -> new FriendshipDTO(
                        f.getId(),
                        f.getFriend().getEmail(),
                        f.getUser().getUsername(),
                        f.getUser().getEmail(),
                        f.getStatus().name(),
                        f.getCreatedAt(),
                        f.getUpdatedAt()))
                .collect(Collectors.toList());
    }



}
