package com.bibliotek.personal.repository;

import com.bibliotek.personal.entity.Book;

import com.bibliotek.personal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {

    List<Book> findByOwner(User owner);
}