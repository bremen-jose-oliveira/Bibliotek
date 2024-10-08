package com.Bibliotek.Personal.dao;

import com.Bibliotek.Personal.entity.Review;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReviewDAOImpl implements ReviewDAO {

    private final EntityManager entityManager;

    @Autowired
    public ReviewDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void save(Review theReview) {
        entityManager.persist(theReview);
    }

    @Override
    public Review findById(Integer id) {
        return entityManager.find(Review.class, id);
    }

    @Override
    public List<Review> findAll() {
        TypedQuery<Review> theQuery = entityManager.createQuery("From Review", Review.class);
        return theQuery.getResultList();
    }

    @Override
    @Transactional
    public void delete(Review theReview) {
        entityManager.remove(entityManager.contains(theReview) ? theReview : entityManager.merge(theReview));
    }
}
