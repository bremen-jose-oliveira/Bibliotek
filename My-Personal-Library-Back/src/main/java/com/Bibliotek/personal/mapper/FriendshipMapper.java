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

        boolean isCurrentUserTheUser = friendship.getUser().getEmail().equals(currentUserEmail);
        User friend = isCurrentUserTheUser ? friendship.getFriend() : friendship.getUser();

        FriendshipDTO friendshipDTO = new FriendshipDTO();

        friendshipDTO.setId(friendship.getId());
        friendshipDTO.setUserEmail(currentUserEmail);
        friendshipDTO.setFriendEmail(friend.getEmail());
        friendshipDTO.setUsername(friend.getUsername());
        friendshipDTO.setFriendshipStatus(friendship.getStatus().name());
        friendshipDTO.setCreatedAt(friendship.getCreatedAt());
        friendshipDTO.setUpdatedAt(friendship.getUpdatedAt());

        return friendshipDTO;
    }
}