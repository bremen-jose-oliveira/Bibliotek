package com.Bibliotek.Personal.controller.User;

import com.Bibliotek.Personal.ApiResponse.ApiResponse;
import com.Bibliotek.Personal.Config.JwtUtil;
import com.Bibliotek.Personal.dao.User.UserDAO;
import com.Bibliotek.Personal.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    public PasswordEncoder passwordEncoder;

    private final UserDAO userDAO;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserDAO userDAO ,AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
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

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createUser(@RequestBody User user) {
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
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {


        System.out.println("Attempting to authenticate user: " + loginRequest.getUsername());

        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), loginRequest.getPassword())
            );

            if (authentication.isAuthenticated()) {

                String token = jwtUtil.generateToken(loginRequest.getUsername());
                System.out.println("Login successful for user: " + loginRequest.getUsername() + " with the token " + token);
                return ResponseEntity.ok(new JwtResponse(token));
            }
        } catch (Exception e) {
            System.out.println("Authentication failed for user: " + loginRequest.getUsername() + ". Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new JwtResponse("Authentication failed"));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new JwtResponse("Authentication failed"));
    }

}
