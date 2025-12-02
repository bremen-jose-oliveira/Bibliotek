package com.bibliotek.personal.config;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Date;
import java.util.Base64;

@Component("appleClientSecretProvider")
public class AppleClientSecretProvider {

    @Value("${APPLE_TEAM_ID:YJYA3WJYT5}")
    private String teamId;

    @Value("${APPLE_KEY_ID:3NJ23GPMBU}")
    private String keyId;

    @Value("${spring.security.oauth2.client.registration.apple.client-id}")
    private String clientId;

    // Path to your downloaded .p8 file
    private static final String PRIVATE_KEY_PATH = "src/main/resources/AuthKey_3NJ23GPMBU.p8";

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
            System.out.println("Generated Apple client_secret: " + signedJWT.serialize());

            return signedJWT.serialize();

        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate Apple client_secret JWT", e);
        }
    }

    private ECPrivateKey loadPrivateKey() throws Exception {
        String key = new String(Files.readAllBytes(Paths.get(PRIVATE_KEY_PATH)))
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
