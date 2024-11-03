package com.Bibliotek.Personal.dao.User;

import com.Bibliotek.Personal.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class UserDAOImpl implements UserDAO{

    private EntityManager entityManager;

    @Autowired
    public UserDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void save(User theUser) {
        entityManager.persist(theUser);

    }
    @Transactional
    @Override
    public User findByUsername(String username) {
        TypedQuery<User> query = entityManager.createQuery("FROM User u WHERE u.username = :username", User.class);
        query.setParameter("username", username);
        return query.getResultStream().findFirst().orElse(null);
    }


    @Override
    public User findById(Integer id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public List<User> findAll() {

        TypedQuery<User> theQuery = entityManager.createQuery("From User", User.class );

        return theQuery.getResultList();
    }

    @Override
    @Transactional
    public void delete(User theUser) {
        entityManager.remove(entityManager.contains(theUser) ? theUser : entityManager.merge(theUser));
    }

}
