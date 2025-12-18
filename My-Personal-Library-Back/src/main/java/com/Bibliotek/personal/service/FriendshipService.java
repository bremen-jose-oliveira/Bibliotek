package com.Bibliotek.personal.service;

import com.Bibliotek.personal.dto.FriendshipDTO;
import com.Bibliotek.personal.entity.Friendship;
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

    @Autowired
    public FriendshipService(UserRepository userRepository, FriendshipRepository friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
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

        return true;

    }

    public boolean approveFriendRequest(String userEmail, String friendEmail) {
        User user = userRepository.findByEmail(userEmail);
        User friend = userRepository.findByEmail(friendEmail);

        if (user == null || friend == null) {
            return false;
        }

        Optional<Friendship> friendshipOptional = friendshipRepository.findByUserAndFriend(friend, user);
        if (friendshipOptional.isPresent()
                && friendshipOptional.get().getStatus() == Friendship.FriendshipStatus.PENDING) {
            Friendship friendship = friendshipOptional.get();
            friendship.setStatus(Friendship.FriendshipStatus.ACCEPTED);
            friendshipRepository.save(friendship);
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

        User user = userRepository.findByEmail(currentUserEmail);
        if (user == null) {
            return List.of();
        }

        List<Friendship> friendships = friendshipRepository.findAllAcceptedFriendships(user);

        return friendships.stream()
                .map(f -> new FriendshipDTO(
                        f.getId(),
                        f.getFriend().getUsername(),
                        f.getFriend().getEmail(),
                        f.getStatus().name(),
                        f.getCreatedAt(),
                        f.getUpdatedAt()))
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
        User user = userRepository.findByEmail(userEmail);
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
