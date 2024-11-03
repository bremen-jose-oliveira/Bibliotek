package com.Bibliotek.Personal.service;



import com.Bibliotek.Personal.dao.User.UserDAO;
import com.Bibliotek.Personal.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private final UserDAO userDAO;

    @Autowired
    public CustomUserDetailsService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            System.out.println("User not found: " + username); // Log user not found
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        System.out.println("User found: " + user.getUsername()); // Log user found
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList() // No roles since you don't want them
        );
    }
}
