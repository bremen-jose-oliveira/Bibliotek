package com.Bibliotek.personal.repository;


import com.Bibliotek.personal.entity.UserBookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBookStatusRepository extends JpaRepository<UserBookStatus, Integer> {
    @Query("SELECT ubs.status FROM UserBookStatus ubs WHERE ubs.book.id = :bookId")
    Optional<UserBookStatus.BookStatus> findStatusByBookId(@Param("bookId") int bookId);


    Optional<UserBookStatus> findByUserIdAndBookId(int userId, int bookId);
    
    List<UserBookStatus> findByUserId(int userId);
}
