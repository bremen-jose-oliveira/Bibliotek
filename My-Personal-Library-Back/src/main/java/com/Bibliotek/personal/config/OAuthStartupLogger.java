package com.Bibliotek.personal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Logs OAuth redirect URIs at startup so we can verify they match Google/Apple console (e.g. when debugging 401).
 */
@Component
public class OAuthStartupLogger implements ApplicationRunner {

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri:not set}")
    private String googleRedirectUri;

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("[OAuth] Google redirect_uri configured as: " + googleRedirectUri);
    }
}
