package com.Bibliotek.Personal.dao;

import com.Bibliotek.Personal.entity.Book;
import com.Bibliotek.Personal.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class BookDAOImpl implements BookDAO{

    private EntityManager entityManager;

    @Autowired
    public BookDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void save(Book theBook) {
        entityManager.persist(theBook);

    }

    @Override
    public Book findById(Integer id) {
        return entityManager.find(Book.class, id);
    }

    @Override
    public List<Book> findAll() {

        TypedQuery<Book> theQuery = entityManager.createQuery("From Book", Book.class );

        return theQuery.getResultList();
    }

    @Override
    @Transactional
    public void delete(Book theBook) {
        entityManager.remove(entityManager.contains(theBook) ? theBook : entityManager.merge(theBook));
    }

}
