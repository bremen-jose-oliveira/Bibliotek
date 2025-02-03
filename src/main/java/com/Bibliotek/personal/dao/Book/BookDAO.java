package com.bibliotek.personal.dao.Book;

import com.bibliotek.personal.entity.Book;
import com.bibliotek.personal.entity.User;

import java.util.List;

public interface BookDAO {
        void save(Book theBook);
        Book findById(Integer id);
        List<Book> findAll();
        void delete(Book theBook);

        List<Book> findByUser(User currentUser);
}
