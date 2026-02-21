package com.Bibliotek.personal.repository;


import com.Bibliotek.personal.entity.Book;
import com.Bibliotek.personal.entity.Review;
import com.Bibliotek.personal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findReviewsByBook(Book book);
    Optional<Review> findByBookAndUser(Book book, User user);
    List<Review> findByUser(User user);
}
