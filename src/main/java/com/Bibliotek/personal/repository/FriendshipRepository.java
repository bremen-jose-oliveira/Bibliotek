package com.bibliotek.personal.repository;

import com.bibliotek.personal.entity.Friendship;
import com.bibliotek.personal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {
    @Query("SELECT f FROM Friendship f WHERE f.user = :user AND f.friend = :friend")
    Optional<Friendship> findByUserAndFriend(@Param("user") User user, @Param("friend") User friend);
    
    @Query("SELECT f FROM Friendship f WHERE f.user = :user")
    List<Friendship> findAllByUser(@Param("user") User user);

    @Query("SELECT f FROM Friendship f WHERE (f.user = :user OR f.friend = :user) AND f.status = 'ACCEPTED'")
    List<Friendship> findAllAcceptedFriendships(@Param("user") User user);

    @Query("SELECT f FROM Friendship f WHERE (f.user = :user OR f.friend = :user) AND f.status = 'ACCEPTED'")
    List<Friendship> findAllUsers(@Param("user") User user);

    @Query("SELECT f FROM Friendship f WHERE f.friend = :user AND f.status = :status")
    List<Friendship> findAllByFriendAndStatus(@Param("user") User user, @Param("status") Friendship.FriendshipStatus friendshipStatus);
}
