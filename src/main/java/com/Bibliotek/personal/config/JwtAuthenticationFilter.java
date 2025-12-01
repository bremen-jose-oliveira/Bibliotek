package com.bibliotek.personal.config;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException, java.io.IOException {
        final String authHeader = request.getHeader("Authorization");
        String email = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // Extract token
            try {
                email = jwtUtil.extractUsername(jwt);
                System.out.println("Extracted email: " + email);
            } catch (ExpiredJwtException e) {
                // Token expired - log and continue without authentication
                // Spring Security will handle authorization for protected endpoints
                System.out.println("JWT token expired: " + e.getMessage());
                // Don't set email, so authentication won't be set
                email = null;
                jwt = null;
            } catch (MalformedJwtException | SignatureException e) {
                // Invalid token - log and continue without authentication
                System.out.println("Invalid JWT token: " + e.getMessage());
                // Don't set email, so authentication won't be set
                email = null;
                jwt = null;
            } catch (Exception e) {
                // Other JWT errors - log and continue without authentication
                System.out.println("JWT error: " + e.getMessage());
                // Don't set email, so authentication won't be set
                email = null;
                jwt = null;
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("Authentication set for user with the email: " + email); // Debug log
                }
            } catch (Exception e) {
                // If validation fails, continue without authentication
                System.out.println("Token validation failed: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
