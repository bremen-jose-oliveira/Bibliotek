package com.Bibliotek.personal.repository;


import com.Bibliotek.personal.entity.Friendship;
import com.Bibliotek.personal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {
    Optional<Friendship> findByUserAndFriend(User user, User friend);
    List<Friendship> findAllByUser(User user);

    @Query("SELECT f FROM Friendship f WHERE (f.user = :user OR f.friend = :user) AND f.status = 'ACCEPTED'")
    List<Friendship> findAllAcceptedFriendships(@Param("user") User user);

    @Query("SELECT f FROM Friendship f WHERE (f.user = :user OR f.friend = :user) AND f.status = 'ACCEPTED'")
    List<Friendship> findAllUsers(@Param("user") User user);


    List<Friendship> findAllByFriendAndStatus(User user, Friendship.FriendshipStatus friendshipStatus);
}
