package com.Bibliotek.personal.repository;


import com.Bibliotek.personal.entity.Book;
import com.Bibliotek.personal.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ReviewRepository  extends JpaRepository<Review, Integer> {

    List<Review> findReviewsByBook(Book book);
}
