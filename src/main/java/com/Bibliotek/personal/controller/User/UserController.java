package com.bibliotek.personal.controller.User;


import com.bibliotek.personal.apiResponse.ApiResponse;
import com.bibliotek.personal.config.JwtUtil;
import com.bibliotek.personal.dto.user.LoginRequest;
import com.bibliotek.personal.dto.user.UserDTO;
import com.bibliotek.personal.dto.user.UserRegistrationDTO;
import com.bibliotek.personal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @Autowired
    private JwtUtil jwtUtil;



    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
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
            userService.createUser(userDTO);
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

    @PutMapping("/current/update")
    public ResponseEntity<ApiResponse> updateCurrentUser(@RequestBody Map<String, String> request, Authentication authentication) {
        try {
            String username = request.get("username");
            userService.updateCurrentUser(authentication.getName(), username);
            return new ResponseEntity<>(new ApiResponse("Username updated successfully", true, 200), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false, 400), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/current/update/password")
    public ResponseEntity<ApiResponse> updateCurrentUserPassword(@RequestBody Map<String, String> request, Authentication authentication) {
        try {
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");
            userService.updateCurrentUserPassword(authentication.getName(), oldPassword, newPassword);
            return new ResponseEntity<>(new ApiResponse("Password updated successfully", true, 200), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false, 400), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/current")
    public ResponseEntity<ApiResponse> deleteCurrentUser(Authentication authentication) {
        try {
            String email = authentication.getName();
            System.out.println("Deleting user account for: " + email);
            userService.deleteUserByEmail(email);
            return new ResponseEntity<>(new ApiResponse("Account deleted successfully", true, 200), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Error deleting user account: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(new ApiResponse(e.getMessage(), false, 400), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            if (loginRequest.getEmail() == null || loginRequest.getEmail().isBlank()) {
                System.out.println("Login failed: Email is null or blank");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            if (loginRequest.getPassword() == null || loginRequest.getPassword().isBlank()) {
                System.out.println("Login failed: Password is null or blank");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            
            System.out.println("Attempting login for email: " + loginRequest.getEmail());
            UserDTO user = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
            String token = jwtUtil.generateToken(user.getEmail());
            System.out.println("Login successful for email: " + loginRequest.getEmail());
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (RuntimeException e) {
            System.out.println("Login failed: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            System.out.println("Unexpected error during login: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}