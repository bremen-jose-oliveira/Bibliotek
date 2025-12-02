package com.bibliotek.personal.config;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Component("appleClientSecretProvider")
public class AppleClientSecretProvider {

    @Value("${APPLE_TEAM_ID:YJYA3WJYT5}")
    private String teamId;

    @Value("${APPLE_KEY_ID:3NJ23GPMBU}")
    private String keyId;

    @Value("${spring.security.oauth2.client.registration.apple.client-id}")
    private String clientId;

    // Apple private key - can be loaded from environment variable or classpath
    @Value("${APPLE_PRIVATE_KEY:}")
    private String privateKeyContent;

    public String generate() {
        try {
            ECPrivateKey privateKey = loadPrivateKey();

            Instant now = Instant.now();
            Instant expires = now.plusSeconds(60 * 60 * 6); // 6 hours max for Apple

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .issuer(teamId)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(expires))
                    .audience("https://appleid.apple.com")
                    .subject(clientId)
                    .build();

            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                    .keyID(keyId)
                    .type(JOSEObjectType.JWT)
                    .build();

            SignedJWT signedJWT = new SignedJWT(header, claimsSet);
            signedJWT.sign(new ECDSASigner(privateKey));
            String secret = signedJWT.serialize();
            System.out.println("Generated Apple client_secret JWT (length: " + secret.length() + ")");
            System.out.println("JWT preview: " + secret.substring(0, Math.min(50, secret.length())) + "...");

            return secret;

        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate Apple client_secret JWT", e);
        }
    }

    private ECPrivateKey loadPrivateKey() throws Exception {
        String keyContent;
        
        // Try to load from environment variable first (for production/Fly.io)
        if (privateKeyContent != null && !privateKeyContent.isEmpty()) {
            keyContent = privateKeyContent;
        } else {
            // Fallback: Load from classpath (for local development)
            String PRIVATE_KEY_PATH = "AuthKey_3NJ23GPMBU.p8";
            InputStream keyStream = getClass().getClassLoader().getResourceAsStream(PRIVATE_KEY_PATH);
            if (keyStream == null) {
                throw new IllegalStateException("Apple private key not found. " +
                    "Either set APPLE_PRIVATE_KEY environment variable with the key content, " +
                    "or place AuthKey_3NJ23GPMBU.p8 in src/main/resources/");
            }
            keyContent = new String(keyStream.readAllBytes(), StandardCharsets.UTF_8);
        }
        
        // Clean the key content
        String key = keyContent
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return (ECPrivateKey) privateKey;
    }
}
