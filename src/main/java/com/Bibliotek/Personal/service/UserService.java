package com.Bibliotek.Personal.service;
/*
import com.Bibliotek.Personal.ApiResponse.ApiResponse;
import com.Bibliotek.Personal.dao.User.UserDAO;
import com.Bibliotek.Personal.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

    public ResponseEntity<ApiResponse> createUser(User user) {

            // Check if username is already taken
            User existingUser = userDAO.findByUsername(user.getUsername());
            if (existingUser != null) {
                return new ResponseEntity<>(new ApiResponse("User already exists"), HttpStatus.BAD_REQUEST);
            }

            // Hash the password
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);

            // Save the new user
            userDAO.save(user);
            return new ResponseEntity<>(new ApiResponse("User created successfully"), HttpStatus.CREATED);
        }
    }
*/