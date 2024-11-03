package com.Bibliotek.Personal.dao.User;

import com.Bibliotek.Personal.entity.User;

import java.util.List;

public interface UserDAO {
    void save(User theUser);
    User findById(Integer id);
    List<User> findAll();
    void delete(User theUser); // New method for deleting a user

    User findByUsername(String username);
}