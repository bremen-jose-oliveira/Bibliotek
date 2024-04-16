package com.Bibliotek.Personal.dao;

import com.Bibliotek.Personal.entity.Book;

import java.util.List;

public interface BookDAO {

        void save(Book theBook);
        Book findById(Integer id);

        List<Book> findAll();




}
