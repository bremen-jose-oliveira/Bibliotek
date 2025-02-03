package com.bibliotek.personal.dao.User;

import com.bibliotek.personal.entity.User;

import java.util.List;

public interface UserDAO {
    void save(User theUser);
    User findById(Integer id);
    User findByEmail(String email);
    List<User> findAll();
    void delete(User theUser); // New method for deleting a user

    User findByUsername(String username);
}