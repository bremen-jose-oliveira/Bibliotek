package com.bibliotek.personal.repository;

import com.bibliotek.personal.entity.Book;
import com.bibliotek.personal.entity.Exchange;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRepository  extends JpaRepository<Exchange, Integer>{
    @Query("SELECT e FROM Exchange e WHERE e.book = :book")
    List<Exchange> findExchangesByBook(@Param("book") Book book);

    @Query("SELECT e FROM Exchange e WHERE e.book.id = :bookId")
    List<Exchange> findAllByBookId(@Param("bookId") int bookId);

    @Query("SELECT e FROM Exchange e WHERE e.borrower.id = :userId")
    List<Exchange> findByBorrowerId(@Param("userId") int userId);
}
