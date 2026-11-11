package com.senai.SAEP.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class CsrfTokenManager {

    private static final String SESSION_ATTRIBUTE = "_csrfToken";

    private final SecureRandom secureRandom = new SecureRandom();

    public String getOrCreateToken(HttpSession session) {
        String token = (String) session.getAttribute(SESSION_ATTRIBUTE);

        if (token == null) {
            byte[] bytes = new byte[32];
            secureRandom.nextBytes(bytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            session.setAttribute(SESSION_ATTRIBUTE, token);
        }

        return token;
    }

    public boolean isValid(HttpSession session, String submittedToken) {
        if (session == null || submittedToken == null || submittedToken.isBlank()) {
            return false;
        }

        String expectedToken = (String) session.getAttribute(SESSION_ATTRIBUTE);
        if (expectedToken == null) {
            return false;
        }

        return MessageDigest.isEqual(
                expectedToken.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                submittedToken.getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );
    }
}
