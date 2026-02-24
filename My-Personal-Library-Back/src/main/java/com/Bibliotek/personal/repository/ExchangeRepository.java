package com.Bibliotek.personal.repository;



import com.Bibliotek.personal.entity.Book;
import com.Bibliotek.personal.entity.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExchangeRepository  extends JpaRepository<Exchange, Integer>{
    List<Exchange> findExchangesByBook(Book book);

    @Query("SELECT e FROM Exchange e " +
           "LEFT JOIN FETCH e.book b " +
           "LEFT JOIN FETCH b.bookDetails " +
           "LEFT JOIN FETCH b.owner " +
           "LEFT JOIN FETCH e.borrower " +
           "WHERE e.id = :id")
    Optional<Exchange> findByIdWithBookAndBorrower(@Param("id") int id);


    @Query("SELECT e.status FROM Exchange e WHERE e.book.id = :bookId")
    Optional<Exchange.ExchangeStatus> findStatusByBookId(@Param("bookId") int bookId);

    List<Exchange> findByBorrowerId(int userId);

    @Query("SELECT DISTINCT e FROM Exchange e " +
           "LEFT JOIN FETCH e.book b " +
           "LEFT JOIN FETCH b.bookDetails " +
           "LEFT JOIN FETCH b.owner " +
           "LEFT JOIN FETCH e.borrower " +
           "WHERE e.borrower.id = :userId")
    List<Exchange> findByBorrowerIdWithBookAndBorrower(@Param("userId") int userId);

    @Query("SELECT e FROM Exchange e WHERE e.book.owner.id = :ownerId")
    List<Exchange> findByBookOwnerId(@Param("ownerId") int ownerId);

    @Query("SELECT DISTINCT e FROM Exchange e " +
           "LEFT JOIN FETCH e.book b " +
           "LEFT JOIN FETCH b.bookDetails " +
           "LEFT JOIN FETCH b.owner " +
           "LEFT JOIN FETCH e.borrower " +
           "WHERE b.owner.id = :ownerId")
    List<Exchange> findByBookOwnerIdWithBookAndBorrower(@Param("ownerId") int ownerId);
}
