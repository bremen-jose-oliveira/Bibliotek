package com.Bibliotek.Personal.Config;


import com.Bibliotek.Personal.controller.User.JwtResponse;
import com.Bibliotek.Personal.service.CustomOAuth2UserService;
import com.Bibliotek.Personal.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.ObjectOutput;
import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults; // Import withDefaults here
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    public JwtUtil jwtUtil;

    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtUtil = jwtUtil;
    }

    @Value("${FrontEnd.url}")
    private String FrontEndUrl;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(FrontEndUrl, "http://localhost:8081","myapp://redirect","/oauth2/authorization/google","https://appleid.apple.com"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Apply CORS to all endpoints
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Apply CORS configuration
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/login").permitAll()
                        .requestMatchers( "/oauth2/**").permitAll()
                        .requestMatchers("/api/users/create").permitAll()
                        .requestMatchers("/api/users/oauth2-login").permitAll()
                        .requestMatchers("/oauth2/authorization/google").permitAll()
                        .requestMatchers("/oauth2/authorization/apple").permitAll()


                        .anyRequest().authenticated() // Protect all other endpoints
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Set stateless sessions
                )

                .oauth2Login(oauth2 -> oauth2

                        .successHandler((request, response, authentication) -> {
                            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

                            System.out.println("\n\n" + "oAuth2User  ----->" + oAuth2User + "\n\n");


                            String email = oAuth2User.getAttribute("email");
                            String username = oAuth2User.getAttribute("name");

                            // Generate the JWT token
                            String token = jwtUtil.generateToken(username);
                            System.out.println("Login successful for user: " + username + " with token: " + token);

                            // Redirect to frontend with the token as a query parameter

                            String redirectUrl = FrontEndUrl+"/(tabs)" + "?token=" + token;
                                    String jsonResponse = "{\"token\":\"" + token + "\"}";
                                    response.getWriter().write(jsonResponse);

                            response.sendRedirect(redirectUrl);
                            response.getWriter().flush();

                        })

                )


                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Add JWT filter
                .httpBasic(withDefaults()); // Enable HTTP Basic authentication

        return http.build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService() {

        return new CustomOAuth2UserService(userDetailsService);
    }


}
