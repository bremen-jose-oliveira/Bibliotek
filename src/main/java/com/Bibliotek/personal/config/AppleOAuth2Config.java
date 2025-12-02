package com.bibliotek.personal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class AppleOAuth2Config {

    @Autowired
    private AppleClientSecretProvider appleClientSecretProvider;

    @Value("${spring.security.oauth2.client.registration.apple.client-id}")
    private String appleClientId;

    @Value("${BACK_END_URL}")
    private String backEndUrl;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${GOOGLE_CLIENT_SECRET}")
    private String googleClientSecret;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = new ArrayList<>();

        // Google registration (from application.yml)
        ClientRegistration googleRegistration = ClientRegistration
                .withRegistrationId("google")
                .clientId(googleClientId)
                .clientSecret(googleClientSecret)
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://www.googleapis.com/oauth2/v4/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .userNameAttributeName("sub")
                .scope("email", "profile")
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();

        registrations.add(googleRegistration);

        // Apple registration (programmatically configured with dynamic client secret)
        // Generate a fresh client secret (JWT) - valid for 6 hours
        String appleClientSecret = appleClientSecretProvider.generate();
        System.out.println("Configuring Apple OAuth2 with client secret: " + appleClientSecret.substring(0, Math.min(50, appleClientSecret.length())) + "...");

        ClientRegistration appleRegistration = ClientRegistration
                .withRegistrationId("apple")
                .clientId(appleClientId)
                .clientSecret(appleClientSecret)
                .authorizationUri("https://appleid.apple.com/auth/authorize?response_mode=form_post")
                .tokenUri("https://appleid.apple.com/auth/token")
                .userNameAttributeName("sub")
                .scope("openid", "email", "name")
                .redirectUri(backEndUrl + "/login/oauth2/code/apple")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();

        registrations.add(appleRegistration);

        return new InMemoryClientRegistrationRepository(registrations);
    }
}

