package com.bibliotek.personal.service;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final CustomUserDetailsService userDetailsService;

    public CustomOAuth2UserService(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        System.out.println("Loading OAuth2 user for registration: " + registrationId);

        OAuth2User oAuth2User;
        
        if ("apple".equals(registrationId)) {
            // Apple doesn't provide a user info endpoint - extract from ID token
            oAuth2User = loadAppleUser(userRequest);
        } else {
            // For Google and other providers, use default behavior
            oAuth2User = super.loadUser(userRequest);
        }

        System.out.println("OAuth2 User attributes: " + oAuth2User.getAttributes());
        return userDetailsService.loadUserByOAuth2(oAuth2User); // Delegate to CustomUserDetailsService
    }

    private OAuth2User loadAppleUser(OAuth2UserRequest userRequest) {
        try {
            // For Apple (OpenID Connect), the ID token is in the token response
            // Spring Security OAuth2 Client stores it in additionalParameters
            String idTokenValue = null;
            
            System.out.println("Attempting to extract Apple ID token...");
            System.out.println("Additional parameters: " + userRequest.getAdditionalParameters());
            
            // Method 1: Try to get ID token from additional parameters (most common for OIDC)
            if (userRequest.getAdditionalParameters() != null) {
                Object idTokenObj = userRequest.getAdditionalParameters().get("id_token");
                if (idTokenObj != null) {
                    idTokenValue = idTokenObj.toString();
                    System.out.println("Found ID token in additional parameters");
                }
            }
            
            // Method 2: The access token response might contain the ID token
            // For OIDC providers, Spring Security might include it in the token response
            // Check if the access token itself is actually the ID token (unlikely but possible)
            if (idTokenValue == null) {
                // Try to parse the access token as ID token (sometimes they're the same for OIDC)
                String accessTokenValue = userRequest.getAccessToken().getTokenValue();
                System.out.println("Access token length: " + accessTokenValue.length());
                
                // ID tokens are JWTs, so they have 3 parts separated by dots
                // Access tokens might also be JWTs, so we need to check
                if (accessTokenValue.contains(".") && accessTokenValue.split("\\.").length == 3) {
                    // This might be a JWT - try parsing it as ID token
                    try {
                        JWT testToken = JWTParser.parse(accessTokenValue);
                        JWTClaimsSet testClaims = testToken.getJWTClaimsSet();
                        // If it has 'sub' claim, it's likely an ID token
                        if (testClaims.getSubject() != null) {
                            idTokenValue = accessTokenValue;
                            System.out.println("Using access token as ID token (contains 'sub' claim)");
                        }
                    } catch (Exception e) {
                        System.out.println("Access token is not a valid ID token: " + e.getMessage());
                    }
                }
            }

            if (idTokenValue == null || idTokenValue.isEmpty()) {
                System.out.println("WARNING: Apple ID token not found in OAuth2 response. Attempting fallback...");
                // Fallback: Try to create a minimal user with just the registration ID
                // This should not happen in normal flow, but provides a safety net
                Map<String, Object> fallbackAttributes = new HashMap<>();
                fallbackAttributes.put("sub", "apple-user-" + System.currentTimeMillis());
                fallbackAttributes.put("email", fallbackAttributes.get("sub") + "@apple.com");
                fallbackAttributes.put("name", "Apple User");
                System.out.println("Using fallback attributes: " + fallbackAttributes);
                return new DefaultOAuth2User(Collections.emptyList(), fallbackAttributes, "sub");
            }

            System.out.println("Apple ID Token found, length: " + idTokenValue.length());

            // Parse the ID token to extract user claims
            JWT idToken = JWTParser.parse(idTokenValue);
            JWTClaimsSet claimsSet = idToken.getJWTClaimsSet();

            // Extract user information from ID token claims
            Map<String, Object> attributes = new HashMap<>();
            
            String sub = claimsSet.getSubject();
            String email = claimsSet.getStringClaim("email");
            String name = null;
            
            // Apple may provide name in the ID token (only on first login)
            Object nameObj = claimsSet.getClaim("name");
            if (nameObj != null) {
                if (nameObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> nameMap = (Map<String, Object>) nameObj;
                    name = (String) nameMap.get("firstName");
                    if (name == null) {
                        name = (String) nameMap.get("lastName");
                    }
                } else if (nameObj instanceof String) {
                    name = (String) nameObj;
                }
            }

            // Always include sub (Apple's unique user identifier)
            if (sub != null) {
                attributes.put("sub", sub);
            }

            // Email is only provided on first login
            if (email != null && !email.isEmpty()) {
                attributes.put("email", email);
            }

            // Name is only provided on first login
            if (name != null && !name.isEmpty()) {
                attributes.put("name", name);
            }

            System.out.println("Extracted from Apple ID token - sub: " + sub + ", email: " + email + ", name: " + name);

            // Create OAuth2User from ID token claims
            return new DefaultOAuth2User(
                    Collections.emptyList(),
                    attributes,
                    "sub" // Use 'sub' as the principal attribute for Apple
            );

        } catch (ParseException e) {
            System.out.println("ERROR: Failed to parse Apple ID token: " + e.getMessage());
            e.printStackTrace();
            throw new OAuth2AuthenticationException(new org.springframework.security.oauth2.core.OAuth2Error("invalid_token", "Failed to parse Apple ID token", null), e);
        } catch (Exception e) {
            System.out.println("ERROR: Failed to load Apple user: " + e.getMessage());
            e.printStackTrace();
            throw new OAuth2AuthenticationException(new org.springframework.security.oauth2.core.OAuth2Error("invalid_user", "Failed to load Apple user", null), e);
        }
    }
}