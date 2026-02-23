package com.Bibliotek.personal.controller;

import com.Bibliotek.personal.config.JwtUtil;
import com.Bibliotek.personal.dto.user.UserDTO;
import com.Bibliotek.personal.entity.PasswordResetToken;
import com.Bibliotek.personal.entity.User;
import com.Bibliotek.personal.mapper.UserMapper;
import com.Bibliotek.personal.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.isEmpty()) {
                throw new IllegalArgumentException("Email is required");
            }
            userService.sendPasswordResetEmail(email);
            return ResponseEntity.ok(Collections.singletonMap("message", "Reset email sent!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("password");

        PasswordResetToken resetToken = userService.validatePasswordResetToken(token);
        if (resetToken == null || resetToken.isExpired()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Invalid or expired token"));
        }

        userService.updateUserPassword(resetToken.getUser(), newPassword);
        return ResponseEntity.ok(Collections.singletonMap("message", "Password updated successfully!"));
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> request) {
        try {
            String accessToken = request.get("accessToken");
            if (accessToken == null || accessToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Access token is required"));
            }

            Map<String, String> googleUserInfo = validateGoogleToken(accessToken);
            if (googleUserInfo == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "Invalid Google access token"));
            }

            String email = googleUserInfo.get("email");
            String name = googleUserInfo.get("name");

            if (email == null || email.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Email not provided by Google"));
            }

            String googleUserId = googleUserInfo.get("id");

            User user = userService.findByEmailIgnoreCase(email);
            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setUsername(name != null && !name.isEmpty() ? name : email.split("@")[0]);
                if (googleUserId != null && !googleUserId.isEmpty()) {
                    user.setOauthProviderId("google:" + googleUserId);
                }
                String randomPassword = "OAuth" + System.currentTimeMillis() + Math.random();
                user.setPassword(new BCryptPasswordEncoder().encode(randomPassword));
                userService.save(user);
            } else {
                if ((user.getOauthProviderId() == null || user.getOauthProviderId().isEmpty())
                        && googleUserId != null && !googleUserId.isEmpty()) {
                    user.setOauthProviderId("google:" + googleUserId);
                    userService.save(user);
                }
            }

            String token = jwtUtil.generateToken(email);
            UserDTO userDTO = UserMapper.toDTO(user);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", userDTO);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Authentication failed: " + e.getMessage()));
        }
    }

    @PostMapping("/apple")
    public ResponseEntity<?> appleLogin(@RequestBody Map<String, String> request) {
        try {
            String identityToken = request.get("identityToken");
            @SuppressWarnings("unused")
            String authorizationCode = request.get("authorizationCode"); // Reserved for future use
            String appleUserId = request.get("user");

            if (identityToken == null || identityToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Identity token is required"));
            }

            Map<String, String> appleUserInfo = validateAppleToken(identityToken);
            if (appleUserInfo == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "Invalid Apple identity token"));
            }

            String email = appleUserInfo.get("email");
            String name = appleUserInfo.get("name");

            String appleSubId = appleUserInfo.get("sub");
            if (appleSubId == null || appleSubId.isEmpty()) {
                appleSubId = appleUserId;
            }

            User user = null;
            if (email != null && !email.isEmpty()) {
                user = userService.findByEmailIgnoreCase(email);
            }

            if (user == null) {
                user = new User();
                if (email != null && !email.isEmpty()) {
                    user.setEmail(email);
                } else {
                    user.setEmail(
                            "apple_" + (appleSubId != null ? appleSubId : System.currentTimeMillis()) + "@apple.oauth");
                }
                user.setUsername(
                        name != null && !name.isEmpty() ? name : (email != null ? email.split("@")[0] : "AppleUser"));
                if (appleSubId != null && !appleSubId.isEmpty()) {
                    user.setOauthProviderId("apple:" + appleSubId);
                }
                String randomPassword = "OAuth" + System.currentTimeMillis() + Math.random();
                user.setPassword(new BCryptPasswordEncoder().encode(randomPassword));
                userService.save(user);
            } else {
                if ((user.getOauthProviderId() == null || user.getOauthProviderId().isEmpty())
                        && appleSubId != null && !appleSubId.isEmpty()) {
                    user.setOauthProviderId("apple:" + appleSubId);
                    userService.save(user);
                }
            }

            String token = jwtUtil.generateToken(user.getEmail());
            UserDTO userDTO = UserMapper.toDTO(user);

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", userDTO);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Authentication failed: " + e.getMessage()));
        }
    }

    /**
     * Validates Google access token and retrieves user information
     */
    @SuppressWarnings("deprecation")
    private Map<String, String> validateGoogleToken(String accessToken) {
        try {
            URL url = new URL("https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + accessToken);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                return null;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JsonNode jsonNode = objectMapper.readTree(response.toString());
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("email", jsonNode.has("email") ? jsonNode.get("email").asText() : null);
            userInfo.put("name", jsonNode.has("name") ? jsonNode.get("name").asText() : null);
            userInfo.put("id", jsonNode.has("id") ? jsonNode.get("id").asText() : null);

            return userInfo;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Validates Apple identity token and extracts user information
     * Note: This is a simplified validation. For production, you should validate
     * the token signature using Apple's public keys.
     */
    private Map<String, String> validateAppleToken(String identityToken) {
        try {
            String[] parts = identityToken.split("\\.");
            if (parts.length != 3) {
                return null;
            }

            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            JsonNode claims = objectMapper.readTree(payload);

            Map<String, String> userInfo = new HashMap<>();

            if (claims.has("email")) {
                userInfo.put("email", claims.get("email").asText());
            }

            if (claims.has("name")) {
                JsonNode nameNode = claims.get("name");
                if (nameNode.isObject()) {
                    String firstName = nameNode.has("firstName") ? nameNode.get("firstName").asText() : "";
                    String lastName = nameNode.has("lastName") ? nameNode.get("lastName").asText() : "";
                    String fullName = (firstName + " " + lastName).trim();
                    if (!fullName.isEmpty()) {
                        userInfo.put("name", fullName);
                    }
                }
            }

            if (claims.has("sub")) {
                userInfo.put("sub", claims.get("sub").asText());
            }

            return userInfo;

        } catch (Exception e) {
            return null;
        }
    }
}