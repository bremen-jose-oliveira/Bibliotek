package com.Bibliotek.Personal.dao.Review;

import com.Bibliotek.Personal.entity.Review;

import java.util.List;

public interface ReviewDAO {
    void save(Review theReview);
    Review findById(Integer id);
    List<Review> findAll();
    void delete(Review theReview); // Method for deleting a review
}
