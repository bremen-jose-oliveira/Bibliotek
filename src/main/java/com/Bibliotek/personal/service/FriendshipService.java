package com.bibliotek.personal.service;

import com.bibliotek.personal.dto.FriendshipDTO;
import com.bibliotek.personal.entity.Friendship;

import com.bibliotek.personal.entity.User;
import com.bibliotek.personal.repository.FriendshipRepository;
import com.bibliotek.personal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        if (existingFriendship.isPresent()) {
            return false;
        }

        Friendship friendship = new Friendship(user, friend, Friendship.FriendshipStatus.PENDING);
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
        if (friendshipOptional.isPresent() && friendshipOptional.get().getStatus() == Friendship.FriendshipStatus.PENDING) {
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
        if (friendshipOptional.isPresent() && friendshipOptional.get().getStatus() == Friendship.FriendshipStatus.PENDING) {
            friendshipRepository.delete(friendshipOptional.get());
            return true;
        }
        return false;
    }

    public List<FriendshipDTO> getFriends(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            return List.of();
        }

        List<Friendship> friendships = friendshipRepository.findAllByUser(user);
        return friendships.stream()
                .filter(f -> f.getStatus() == Friendship.FriendshipStatus.ACCEPTED)
                .map(f -> new FriendshipDTO(f.getId(), f.getUser().getEmail(), f.getFriend().getEmail(), f.getStatus().name(), f.getCreatedAt(), f.getUpdatedAt()))
                .collect(Collectors.toList());
    }
}
