package com.Bibliotek.personal.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    /** Fallback for local dev when JWT_SECRET_KEY is not set; must be Base64-encoded. */
    private static final String DEFAULT_SECRET_KEY = "MYTOKENyourBase64EncodedSecretKey12345QWERTY";

    @Value("${Jwt.key:}")
    private String secretKeyBase64;

    @Value("${Jwt.expiration-hours:168}")
    private long expirationHours;

    public SecretKey getSecretKey() {
        String key = (secretKeyBase64 != null && !secretKeyBase64.isBlank()) ? secretKeyBase64 : DEFAULT_SECRET_KEY;
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        long expirationMs = expirationHours * 60 * 60 * 1000L;
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .and()
                .signWith(getSecretKey())
                .compact();
    }

    public Boolean validateToken(String token, String email) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(email) && !isTokenExpired(token));
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();


    }

    private Boolean isTokenExpired (String token) {
        return extractAllClaims(token).getExpiration().before(new Date());

    }

    /**
     * Extracts the subject (email) from a JWT even if it is expired.
     * Signature is still verified; only expiration is ignored.
     * Used for refresh: exchange an expired (but valid-signature) token for a new one.
     * @return email from token subject, or null if token is invalid or signature wrong
     */
    public String extractEmailAllowExpired(String token) {
        if (token == null || token.isBlank()) return null;
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        } catch (SignatureException | IllegalArgumentException e) {
            return null;
        }
    }

}
