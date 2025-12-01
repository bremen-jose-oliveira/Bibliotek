package com.bibliotek.personal.repository;

import com.bibliotek.personal.entity.UserBookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBookStatusRepository extends JpaRepository<UserBookStatus, Integer> {
    @Query("SELECT ubs FROM UserBookStatus ubs WHERE ubs.book.id = :bookId")
    List<UserBookStatus> findAllByBookId(@Param("bookId") int bookId);

    @Query("SELECT ubs FROM UserBookStatus ubs WHERE ubs.book.id = :bookId AND ubs.user.id = :userId")
    Optional<UserBookStatus> findStatusByBookIdAndUserId(@Param("bookId") int bookId, @Param("userId") int userId);

    @Query("SELECT ubs FROM UserBookStatus ubs WHERE ubs.user.id = :userId AND ubs.book.id = :bookId")
    Optional<UserBookStatus> findByUserIdAndBookId(@Param("userId") int userId, @Param("bookId") int bookId);
    
    @Query("SELECT ubs FROM UserBookStatus ubs WHERE ubs.user.id = :userId")
    List<UserBookStatus> findByUserId(@Param("userId") int userId);
}
