package com.Bibliotek.Personal.dao;

import com.Bibliotek.Personal.entity.User;

import java.util.List;

public interface UserDAO {
    void save(User theUser);
    User findById(Integer id);
    List<User> findAll();
    void delete(User theUser); // New method for deleting a user
}