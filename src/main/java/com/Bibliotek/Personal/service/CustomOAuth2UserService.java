package com.Bibliotek.Personal.service;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final CustomUserDetailsService userDetailsService;

    public CustomOAuth2UserService(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("Apple OAuth2 User: " + oAuth2User.getAttributes());
        return userDetailsService.loadUserByOAuth2(oAuth2User); // Delegate to CustomUserDetailsService
    }
}