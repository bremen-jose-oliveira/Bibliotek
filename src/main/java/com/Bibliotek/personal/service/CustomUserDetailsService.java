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
        // Extract user information from OAuth2 provider (Google, Facebook, Apple, etc.)
        String email = oAuth2User.getAttribute("email");
        String username = oAuth2User.getAttribute("name");
        String sub = oAuth2User.getAttribute("sub"); // Apple unique user ID

        // Apple: On subsequent logins, email and name may be null, but sub is always present
        if ((email == null || email.isEmpty()) && sub != null && !sub.isEmpty()) {
            email = sub + "@apple.com"; // Fallback email for Apple users
            username = "AppleUser-" + sub.substring(0, Math.min(8, sub.length()));
        }

        System.out.println("OAuth2 User attributes: " + oAuth2User.getAttributes());
        System.out.println("OAuth2 User resolved email: " + email + ", username: " + username + ", sub: " + sub);

        // Check if user already exists in the database
        // First, try to find by OAuth provider ID (sub) - this works for subsequent Apple logins
        User user = null;
        if (sub != null && !sub.isEmpty()) {
            user = userService.findByOauthProviderId(sub);
            System.out.println("User lookup by OAuth provider ID (sub): " + (user != null ? "Found" : "Not found"));
        }
        
        // If not found by provider ID, try to find by email
        if (user == null) {
            user = userService.findByEmail(email);
            System.out.println("User lookup by email: " + (user != null ? "Found" : "Not found"));
        }
        
        // If user doesn't exist, create a new one
        if (user == null) {
            user = new User();
            user.setUsername(username);
            user.setEmail(email);
            if (sub != null && !sub.isEmpty()) {
                user.setOauthProviderId(sub); // Store the OAuth provider ID for future lookups
            }
            user.setPassword("O@uth2" + Math.random());
            System.out.printf("O@uth2" + Math.random());
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);
            userService.save(user); // Save the new user to the database
            System.out.println("Created new OAuth2 user: " + username + " (email: " + email + ", providerId: " + sub + ")");
        } else {
            // Update OAuth provider ID if it's missing (for users created before this field was added)
            if (sub != null && !sub.isEmpty() && (user.getOauthProviderId() == null || user.getOauthProviderId().isEmpty())) {
                user.setOauthProviderId(sub);
                userService.save(user);
                System.out.println("Updated existing user with OAuth provider ID: " + sub);
            }
        }

        System.out.println("OAuth2 User found/created: " + username + " (email: " + email + ")");

        // Ensure email is always in attributes (Apple may not provide it on subsequent logins)
        java.util.Map<String, Object> attributes = new java.util.HashMap<>(oAuth2User.getAttributes());
        if (!attributes.containsKey("email") || attributes.get("email") == null) {
            attributes.put("email", email);
        }
        if (!attributes.containsKey("name") || attributes.get("name") == null) {
            attributes.put("name", username);
        }

        // Use 'sub' as the principal attribute for Apple, 'name' for others
        String principalAttribute = (sub != null) ? "sub" : "name";

        // Return the DefaultOAuth2User, which is suitable for OAuth2 login
        return new DefaultOAuth2User(
                Collections.emptyList(),  // No roles are being added for now
                attributes,  // All user attributes with guaranteed email and name
                principalAttribute // Principal attribute from OAuth2 provider
        );
    }
}
