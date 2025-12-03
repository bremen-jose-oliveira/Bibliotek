package com.bibliotek.personal.config;


import com.bibliotek.personal.entity.User;
import com.bibliotek.personal.service.CustomOAuth2UserService;
import com.bibliotek.personal.service.CustomOidcUserService;
import com.bibliotek.personal.service.CustomUserDetailsService;
import com.bibliotek.personal.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;
    public JwtUtil jwtUtil;

    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter, JwtUtil jwtUtil, UserService userService) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
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
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers( "/oauth2/**").permitAll()
                        .requestMatchers("/api/users/create").permitAll()
                        .requestMatchers("/api/users/oauth2-login").permitAll()
                        .requestMatchers("/oauth2/authorization/**").permitAll()
                        .requestMatchers("/oauth2/authorization/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()



                        .anyRequest().authenticated() // Protect all other endpoints
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Set stateless sessions
                )

                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(customOidcUserService()) // For Apple (OIDC)
                                .userService(customOAuth2UserService()) // For Google (OAuth2)
                        )
                        .successHandler((request, response, authentication) -> {
                            System.out.println("\n\n========== OAUTH2 SUCCESS HANDLER STARTED ==========");
                            System.out.println("Request URI: " + request.getRequestURI());
                            System.out.println("Authentication: " + authentication);
                            
                            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

                            System.out.println("\n\n" + "oAuth2User  ----->" + oAuth2User + "\n\n");
                            System.out.println("OAuth2User attributes: " + oAuth2User.getAttributes());
                            System.out.println("OAuth2User name: " + oAuth2User.getName());

                            // Email should always be present now (CustomUserDetailsService ensures it)
                            String email = oAuth2User.getAttribute("email");
                            String username = oAuth2User.getAttribute("name");
                            String sub = oAuth2User.getAttribute("sub");

                            System.out.println("Extracted - email: " + email + ", username: " + username + ", sub: " + sub);

                            if (email == null || email.isEmpty()) {
                                System.out.println("❌ ERROR: Email is missing from OAuth2User attributes");
                                System.out.println("All attributes: " + oAuth2User.getAttributes());
                                response.sendError(400, "Unable to determine user email");
                                return;
                            }

                            // Verify user exists in database (should have been created by CustomUserDetailsService)
                            try {
                                User user = userService.findByEmail(email);
                                if (user == null && sub != null) {
                                    user = userService.findByOauthProviderId(sub);
                                }
                                if (user == null) {
                                    System.out.println("⚠️ WARNING: User not found in database after OAuth login!");
                                    System.out.println("Email: " + email + ", Sub: " + sub);
                                    System.out.println("This should not happen - user should have been created by CustomUserDetailsService");
                                } else {
                                    System.out.println("✅ User found in database: ID=" + user.getId() + ", email=" + user.getEmail() + ", oauthProviderId=" + user.getOauthProviderId());
                                }
                            } catch (Exception e) {
                                System.out.println("❌ ERROR checking user in database: " + e.getMessage());
                                e.printStackTrace();
                            }

                            // Generate the JWT token
                            String token = jwtUtil.generateToken(email);
                            System.out.println("✅ Login successful for user: " + username + " (email: " + email + ") with token: " + token.substring(0, Math.min(50, token.length())) + "...");

                            // Redirect to frontend with the token as a query parameter
                            String redirectUrl = FrontEndUrl+"/(tabs)" + "?token=" + token;
                            String jsonResponse = "{\"token\":\"" + token + "\"}";
                            System.out.println("Redirecting to: " + redirectUrl);
                            response.getWriter().write(jsonResponse);
                            response.sendRedirect(redirectUrl);
                            response.getWriter().flush();
                            
                            System.out.println("========== OAUTH2 SUCCESS HANDLER COMPLETED ==========\n\n");

                        })
                        .failureHandler((request, response, exception) -> {
                            System.out.println("\n\n========== OAUTH2 FAILURE HANDLER ==========");
                            System.out.println("OAuth login failed: " + exception.getMessage());
                            exception.printStackTrace();
                            
                            // Redirect to frontend with error parameter
                            String redirectUrl = FrontEndUrl + "/(tabs)?error=oauth_failed";
                            System.out.println("Redirecting to: " + redirectUrl);
                            response.sendRedirect(redirectUrl);
                            
                            System.out.println("========== OAUTH2 FAILURE HANDLER COMPLETE ==========\n\n");
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

    @Bean
    public CustomOidcUserService customOidcUserService() {
        return new CustomOidcUserService(userDetailsService);
    }
}
