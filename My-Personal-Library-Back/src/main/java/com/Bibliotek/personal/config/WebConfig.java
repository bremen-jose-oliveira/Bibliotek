package com.Bibliotek.personal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class WebConfig {

    @Value("${FrontEnd.url}")
    private String FrontEndUrl;
    @Bean
    public CorsFilter corsFilter() {


        CorsConfiguration config = new CorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        config.setAllowedOrigins(Arrays.asList(FrontEndUrl, "http://localhost:8081","myapp://redirect","/oauth2/authorization/google","https://appleid.apple.com"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE","OPTIONS", "HEAD"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        config.setAllowCredentials(true); // Allow cookies for cross-origin requests

        source.registerCorsConfiguration("/**", config); // Apply CORS to all endpoints
        return new CorsFilter(source);
    }
}
