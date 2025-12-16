package com.Bibliotek.personal.repository;


import com.Bibliotek.personal.entity.Book;
import com.Bibliotek.personal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {


    @Query("SELECT b FROM Book b WHERE b.owner = :owner")
    List<Book> findByOwner(@Param("owner") User owner);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.reviews WHERE b.id = :bookId")
    Optional<Book> findByIdWithReviews(@Param("bookId") int bookId);



    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.reviews LEFT JOIN FETCH b.exchanges WHERE b.owner = :owner")
    Optional<Book> findBookWithDetailsByOwner(@Param("owner") User owner);


}