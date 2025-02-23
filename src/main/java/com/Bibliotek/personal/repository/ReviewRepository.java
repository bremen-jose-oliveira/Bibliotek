package com.bibliotek.personal.repository;

import com.bibliotek.personal.entity.Book;
import com.bibliotek.personal.entity.Review;
import com.bibliotek.personal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ReviewRepository  extends JpaRepository<Review, Integer> {

    List<Review> findReviewsByBook(Book book);
}
