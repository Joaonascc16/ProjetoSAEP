package com.senai.SAEP.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.jupiter.api.Assertions.*;

class CsrfTokenManagerTest {

    private final CsrfTokenManager csrfTokenManager = new CsrfTokenManager();

    @Test
    void deveCriarTokenEReutilizaLoNaMesmaSessao() {
        MockHttpSession session = new MockHttpSession();

        String firstToken = csrfTokenManager.getOrCreateToken(session);
        String secondToken = csrfTokenManager.getOrCreateToken(session);

        assertNotNull(firstToken);
        assertTrue(firstToken.length() >= 40);
        assertEquals(firstToken, secondToken);
        assertTrue(csrfTokenManager.isValid(session, firstToken));
    }

    @Test
    void deveRejeitarTokenAusenteOuIncorreto() {
        MockHttpSession session = new MockHttpSession();
        String validToken = csrfTokenManager.getOrCreateToken(session);

        assertFalse(csrfTokenManager.isValid(session, null));
        assertFalse(csrfTokenManager.isValid(session, "token-invalido"));
        assertFalse(csrfTokenManager.isValid(null, validToken));
    }

    @Test
    void deveGerarTokensDiferentesParaSessoesDiferentes() {
        String first = csrfTokenManager.getOrCreateToken(new MockHttpSession());
        String second = csrfTokenManager.getOrCreateToken(new MockHttpSession());

        assertNotEquals(first, second);
    }
}
