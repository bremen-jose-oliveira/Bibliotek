package com.flexiserve.flexidemo.dao;

import com.flexiserve.flexidemo.entity.User;

import java.util.List;

public interface UserDAO {
    void save(User theUser);
    User findById(Integer id);

    List <User> findAll();
}
