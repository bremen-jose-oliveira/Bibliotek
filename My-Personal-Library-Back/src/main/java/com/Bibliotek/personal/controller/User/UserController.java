package com.Bibliotek.personal.controller.User;



import com.Bibliotek.personal.apiResponse.ApiResponse;
import com.Bibliotek.personal.config.JwtUtil;
import com.Bibliotek.personal.dto.user.LoginRequest;
import com.Bibliotek.personal.dto.user.UserDTO;
import com.Bibliotek.personal.dto.user.UserRegistrationDTO;
import com.Bibliotek.personal.service.UserService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.Bibliotek.personal.entity.User;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @Autowired
    private JwtUtil jwtUtil;



    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> getAllByUsernameOrEmail(@RequestParam String search) {
        // Call the service method with the search term
        List<UserDTO> users = userService.getUsersByEmailOrUsername(search);

        // Return the list of UserDTOs as the response body
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable int id) {
        UserDTO user = userService.getUserById(id);
        return (user != null) ? new ResponseEntity<>(user, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createUser(@RequestBody UserRegistrationDTO userDTO) {
        try {
            UserDTO createdUser = userService.createUser(userDTO);
            return new ResponseEntity<>(new ApiResponse("User created successfully", true, 201), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false, 400), HttpStatus.BAD_REQUEST);
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable int id, @RequestBody UserDTO userDTO) {
        try {
            UserDTO updatedUser = userService.updateUser(id, userDTO);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable int id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(new ApiResponse("User deleted successfully", true, 204), HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse("User not found", false, 404), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            UserDTO user = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
            String token = jwtUtil.generateToken(user.getEmail());
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/current/update")
    public ResponseEntity<ApiResponse> updateCurrentUser(@RequestBody Map<String, String> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            User user = userService.findByEmail(email);
            if (user == null) {
                return new ResponseEntity<>(new ApiResponse("User not found", false, 404), HttpStatus.NOT_FOUND);
            }
            
            String username = request.get("username");
            if (username != null && !username.trim().isEmpty()) {
                user.setUsername(username);
                userService.save(user);
                return new ResponseEntity<>(new ApiResponse("Username updated successfully", true, 200), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ApiResponse("Username cannot be empty", false, 400), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false, 500), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/current/update/password")
    public ResponseEntity<ApiResponse> updateCurrentUserPassword(@RequestBody Map<String, String> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            User user = userService.findByEmail(email);
            if (user == null) {
                return new ResponseEntity<>(new ApiResponse("User not found", false, 404), HttpStatus.NOT_FOUND);
            }
            
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");
            
            if (oldPassword == null || newPassword == null) {
                return new ResponseEntity<>(new ApiResponse("Old password and new password are required", false, 400), HttpStatus.BAD_REQUEST);
            }
            
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                return new ResponseEntity<>(new ApiResponse("Old password is incorrect", false, 400), HttpStatus.BAD_REQUEST);
            }
            
            userService.updateUserPassword(user, newPassword);
            return new ResponseEntity<>(new ApiResponse("Password updated successfully", true, 200), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false, 500), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}