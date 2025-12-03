package com.bibliotek.personal.service;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class CustomOidcUserService extends OidcUserService {

    private final CustomUserDetailsService userDetailsService;

    public CustomOidcUserService(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("\n\n========== CUSTOM OIDC USER SERVICE ==========");
        System.out.println("Loading OIDC user for registration: " + userRequest.getClientRegistration().getRegistrationId());
        
        // First, let the default OidcUserService load the user (this extracts the ID token)
        OidcUser oidcUser = super.loadUser(userRequest);
        
        System.out.println("OIDC User loaded - email: " + oidcUser.getEmail() + ", sub: " + oidcUser.getSubject());
        System.out.println("OIDC User attributes: " + oidcUser.getAttributes());
        
        // Now delegate to our CustomUserDetailsService to create/find the user in the database
        // This will create the user in the database if it doesn't exist
        userDetailsService.loadUserByOAuth2(oidcUser);
        
        System.out.println("✅ UserDetailsService processed OIDC user - user should be created in database");
        System.out.println("========== CUSTOM OIDC USER SERVICE COMPLETE ==========\n\n");
        
        // Return the OidcUser (we can't change the return type, but the user is created in the database)
        return oidcUser;
    }
}

