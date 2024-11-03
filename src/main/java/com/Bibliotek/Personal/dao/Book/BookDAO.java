package com.Bibliotek.Personal.dao.Book;

import com.Bibliotek.Personal.entity.Book;
import com.Bibliotek.Personal.entity.User;

import java.util.List;

public interface BookDAO {
        void save(Book theBook);
        Book findById(Integer id);
        List<Book> findAll();
        void delete(Book theBook);

        List<Book> findByUser(User currentUser);
}
