package com.Bibliotek.personal.service;

import com.Bibliotek.personal.dto.FriendshipDTO;
import com.Bibliotek.personal.entity.Friendship;
import com.Bibliotek.personal.entity.Notification;
import com.Bibliotek.personal.entity.User;
import com.Bibliotek.personal.repository.FriendshipRepository;
import com.Bibliotek.personal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendshipService {
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final NotificationService notificationService;

    @Autowired
    public FriendshipService(UserRepository userRepository, FriendshipRepository friendshipRepository,
                             NotificationService notificationService) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.notificationService = notificationService;
    }

    public boolean sendFriendRequest(String userEmail, String friendEmail) {
        User user = userRepository.findByEmailIgnoreCase(userEmail).orElse(null);
        User friend = userRepository.findByEmailIgnoreCase(friendEmail).orElse(null);

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

        notificationService.createNotification(
                friend.getEmail(),
                Notification.NotificationType.FRIEND_REQUEST,
                "Friend request",
                user.getUsername() + " sent you a friend request",
                user.getEmail(),
                null,
                friendship.getId());

        return true;

    }

    public boolean approveFriendRequest(String userEmail, String friendEmail) {
        User user = userRepository.findByEmailIgnoreCase(userEmail).orElse(null);
        User friend = userRepository.findByEmailIgnoreCase(friendEmail).orElse(null);

        if (user == null || friend == null) {
            return false;
        }

        Optional<Friendship> friendshipOptional = friendshipRepository.findByUserAndFriend(friend, user);
        if (friendshipOptional.isPresent()
                && friendshipOptional.get().getStatus() == Friendship.FriendshipStatus.PENDING) {
            Friendship friendship = friendshipOptional.get();
            friendship.setStatus(Friendship.FriendshipStatus.ACCEPTED);
            friendshipRepository.save(friendship);

            notificationService.createNotification(
                    friend.getEmail(),
                    Notification.NotificationType.FRIEND_REQUEST_ACCEPTED,
                    "Friend request accepted",
                    user.getUsername() + " accepted your friend request",
                    user.getEmail(),
                    null,
                    friendship.getId());

            return true;
        }
        return false;
    }

    public boolean rejectFriendRequest(String userEmail, String friendEmail) {
        User user = userRepository.findByEmailIgnoreCase(userEmail).orElse(null);
        User friend = userRepository.findByEmailIgnoreCase(friendEmail).orElse(null);

        if (user == null || friend == null) {
            return false;
        }

        Optional<Friendship> friendshipOptional = friendshipRepository.findByUserAndFriend(friend, user);
        if (friendshipOptional.isPresent()
                && friendshipOptional.get().getStatus() == Friendship.FriendshipStatus.PENDING) {
            friendshipRepository.delete(friendshipOptional.get());
            return true;
        }
        return false;
    }

    public List<FriendshipDTO> getFriends() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        User user = userRepository.findByEmailIgnoreCase(currentUserEmail).orElse(null);
        if (user == null) {
            return List.of();
        }

        List<Friendship> friendships = friendshipRepository.findAllAcceptedFriendships(user);

        return friendships.stream()
                .map(f -> {
                    // When current user accepted someone else's request, row is (user=other, friend=currentUser).
                    // When current user sent request that was accepted, row is (user=currentUser, friend=other).
                    // Always show the "other" user's username/email, not the current user's.
                    User other = f.getUser().getEmail().equalsIgnoreCase(currentUserEmail)
                            ? f.getFriend()
                            : f.getUser();
                    return new FriendshipDTO(
                            f.getId(),
                            other.getUsername(),
                            other.getEmail(),
                            f.getStatus().name(),
                            f.getCreatedAt(),
                            f.getUpdatedAt());
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

    public List<FriendshipDTO> getIncomingFriendRequests(String userEmail) {
        User user = userRepository.findByEmailIgnoreCase(userEmail).orElse(null);
        if (user == null) {
            return List.of();
        }

        List<Friendship> incomingRequests = friendshipRepository.findAllByFriendAndStatus(user,
                Friendship.FriendshipStatus.PENDING);

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
