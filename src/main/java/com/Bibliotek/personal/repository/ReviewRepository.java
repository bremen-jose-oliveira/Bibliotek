package com.bibliotek.personal.repository;

import com.bibliotek.personal.entity.Book;
import com.bibliotek.personal.entity.Review;
import com.bibliotek.personal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ReviewRepository  extends JpaRepository<Review, Integer> {

    @Query("SELECT r FROM Review r WHERE r.book = :book")
    List<Review> findReviewsByBook(@Param("book") Book book);
    
    @Query("SELECT r FROM Review r WHERE r.book = :book AND r.user = :user")
    Optional<Review> findByBookAndUser(@Param("book") Book book, @Param("user") User user);
    
    @Query("SELECT r FROM Review r WHERE r.user = :user")
    List<Review> findByUser(@Param("user") User user);
}
