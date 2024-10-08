package com.Bibliotek.Personal.controller;

import com.Bibliotek.Personal.dao.UserDAO;
import com.Bibliotek.Personal.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users") // Base URL for user-related requests
public class UserController {

    private final UserDAO userDAO;

    @Autowired
    public UserController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GetMapping // Get all users
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    @GetMapping("/{id}") // Get a user by ID
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        User user = userDAO.findById(id);
        return (user != null) ? new ResponseEntity<>(user, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping // Create a new user
    public ResponseEntity<User> createUser(@RequestBody User user) {
        userDAO.save(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/{id}") // Update an existing user
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User user) {
        User existingUser = userDAO.findById(id);
        if (existingUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        user.setId(id); // Ensure the ID is set for the update
        userDAO.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{id}") // Delete a user
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        User existingUser = userDAO.findById(id);
        if (existingUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userDAO.delete(existingUser); // Add a delete method in UserDAO
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
