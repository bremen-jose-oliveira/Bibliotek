package com.bibliotek.personal.service;


import com.bibliotek.personal.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    // This method handles traditional username/password login
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findByEmail(email);
        if (user == null) {
            System.out.println("email not found: " + email); // Log user not found
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        System.out.println("User found: " + user.getUsername()); // Log user found
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList() // No roles for now
        );
    }

    // This method handles OAuth2 login
    public OAuth2User loadUserByOAuth2(OAuth2User oAuth2User) {
        // Extract user information from OAuth2 provider (Google, Facebook, etc.)
        String username = oAuth2User.getAttribute("name"); // Adjust attribute based on your OAuth provider
//        String username = oAuth2User.getAttribute("sub");
        System.out.println("Apple OAuth2 User: " + oAuth2User.getAttributes());

        System.out.println("<---oAuth2User ------->" + oAuth2User );


        String email = oAuth2User.getAttribute("email");

        // Check if user already exists in the database or create a new one
        User user = userService.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setUsername(username);
            user.setEmail(email);


            user.setPassword("O@uth2"+ Math.random());
            System.out.printf("O@uth2"+ Math.random());

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);
            userService.save(user); // Save the new user to the database
        }


        System.out.println("OAuth2 User found: " + username);

        // Return the DefaultOAuth2User, which is suitable for OAuth2 login
        return new DefaultOAuth2User(
                Collections.emptyList(),  // No roles are being added for now
                oAuth2User.getAttributes(),  // All user attributes (e.g., email, name)
                "name" // Principal attribute from OAuth2 provider (Google, Facebook, etc.)
        );
    }
}
