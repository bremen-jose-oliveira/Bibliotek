package com.bibliotek.personal.mapper;

import com.bibliotek.personal.dto.FriendshipDTO;
import com.bibliotek.personal.entity.Friendship;

public class FriendshipMapper {

    public static FriendshipDTO toDTO(Friendship friendship) {
        FriendshipDTO friendshipDTO = new FriendshipDTO();
        friendshipDTO.setId(friendship.getId());

        // ✅ Include user and friend emails
        friendshipDTO.setUserEmail(friendship.getUser().getEmail());
        friendshipDTO.setFriendEmail(friendship.getFriend().getEmail());

        // ✅ Convert Enum to String using .name()
        friendshipDTO.setFriendshipStatus(friendship.getStatus().name());

        friendshipDTO.setCreatedAt(friendship.getCreatedAt());
        friendshipDTO.setUpdatedAt(friendship.getUpdatedAt());

        return friendshipDTO;
    }
}
