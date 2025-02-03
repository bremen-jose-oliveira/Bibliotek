package com.bibliotek.personal.controller.User;

import com.bibliotek.personal.apiResponse.ApiResponse;
import com.bibliotek.personal.config.JwtUtil;
import com.bibliotek.personal.dao.User.UserDAO;
import com.bibliotek.personal.entity.User;
import com.bibliotek.personal.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserDAO userDAO;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserDAO userDAO, CustomUserDetailsService customUserDetailsService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.customUserDetailsService = customUserDetailsService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDAO = userDAO;
    }

    // Get all users
    @GetMapping
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }
/*
    @PostMapping("/oauth2/login")
    public ResponseEntity<JwtResponse> appleLogin(@RequestBody Map<String, Object> userData) {
        System.out.println("Apple OAuth2 Login Data: " + userData);

        try {
            String email = (String) userData.get("email");
            String sub = (String) userData.get("sub"); // Unique Apple user ID

            if (email == null) {
                email = "unknown@" + sub + ".appleid"; // Apple might not return email every time
            }

            User user = userDAO.findByEmail(email);
            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setUsername(sub); // Use sub as username if needed
                user.setPassword("O@uth2" + Math.random());

                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                String hashedPassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(hashedPassword);
                userDAO.save(user);
            }

            String token = jwtUtil.generateToken(user.getUsername());
            System.out.println("Apple OAuth2 Login successful for: " + user.getUsername() + " Token: " + token);

            return ResponseEntity.ok(new JwtResponse(token));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new JwtResponse("Error during Apple OAuth2 login: " + e.getMessage()));
        }
    }
*/


    @PostMapping("/oauth2/login")
    public ResponseEntity<JwtResponse> googleLogin(@RequestBody OAuth2User oAuth2User) {

        System.out.println("Oauth User Response" + oAuth2User);

        try {
            String email = oAuth2User.getAttribute("email");
            String username = oAuth2User.getAttribute("name");
            String sub= oAuth2User.getAttribute("sub");


            if (email == null) {
                email = "unknown@" + sub + ".appleid"; // Apple might not return email every time
            }



            if (email == null || username == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new JwtResponse("Missing email or username from OAuth2 response"));
            }

            User user = userDAO.findByEmail(email);
            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setUsername(username);
                userDAO.save(user);  // Create new user
            }

            String token = jwtUtil.generateToken(user.getUsername());
            System.out.println("Login successful for Oauth2 user: " + user.getUsername() + " with the token " + token);
            return ResponseEntity.ok(new JwtResponse(token));


        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new JwtResponse("Error during OAuth2 login: " + e.getMessage()));
        }
    }

    // Get a user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        User user = userDAO.findById(id);
        return (user != null) ? new ResponseEntity<>(user, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Update an existing user
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User user) {
        User existingUser = userDAO.findById(id);
        if (existingUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        user.setId(id);  // Ensure the ID is set for the update
        userDAO.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // Delete a user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        User existingUser = userDAO.findById(id);
        if (existingUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userDAO.delete(existingUser);  // Add a delete method in UserDAO
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Create a new user
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createUser(@RequestBody User user) {
        // Check if username or email already exists
        User existingUser = userDAO.findByUsername(user.getUsername());
        User existingEmail = userDAO.findByEmail(user.getEmail());

        System.out.println(user.getPassword());
        if (existingUser != null || existingEmail != null) {
            return new ResponseEntity<>(new ApiResponse("User or email already exists"), HttpStatus.BAD_REQUEST);
        }

        // Hash the password
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        userDAO.save(user);
        return new ResponseEntity<>(new ApiResponse("User created successfully"), HttpStatus.CREATED);
    }

    // Login for regular users
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
