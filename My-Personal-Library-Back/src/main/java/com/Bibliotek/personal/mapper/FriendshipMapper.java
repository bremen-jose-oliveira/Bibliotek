package com.Bibliotek.personal.mapper;


import com.Bibliotek.personal.dto.FriendshipDTO;
import com.Bibliotek.personal.entity.Friendship;
import com.Bibliotek.personal.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class FriendshipMapper {

    public static FriendshipDTO toDTO(Friendship friendship) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        // Check which one is the current user and which one is the friend
        boolean isCurrentUserTheUser = friendship.getUser().getEmail().equals(currentUserEmail);
        User friend = isCurrentUserTheUser ? friendship.getFriend() : friendship.getUser();

        FriendshipDTO friendshipDTO = new FriendshipDTO();

        friendshipDTO.setId(friendship.getId());
        friendshipDTO.setUserEmail(currentUserEmail);  // Always the email of the logged-in user
        friendshipDTO.setFriendEmail(friend.getEmail());  // Always the email of the friend
        friendshipDTO.setUsername(friend.getUsername());  // Username of the friend
        friendshipDTO.setFriendshipStatus(friendship.getStatus().name());
        friendshipDTO.setCreatedAt(friendship.getCreatedAt());
        friendshipDTO.setUpdatedAt(friendship.getUpdatedAt());

        return friendshipDTO;
    }
}