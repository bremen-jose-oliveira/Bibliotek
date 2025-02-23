package com.bibliotek.personal.repository;

import com.bibliotek.personal.entity.Friendship;
import com.bibliotek.personal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {
    Optional<Friendship> findByUserAndFriend(User user, User friend);
    List<Friendship> findAllByUser(User user);
}
