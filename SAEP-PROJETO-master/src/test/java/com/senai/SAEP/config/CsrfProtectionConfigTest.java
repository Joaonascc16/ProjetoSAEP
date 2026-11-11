package com.senai.SAEP.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.jupiter.api.Assertions.*;

class CsrfProtectionConfigTest {

    private final CsrfTokenManager tokenManager = new CsrfTokenManager();
    private final CsrfProtectionConfig.CsrfInterceptor interceptor =
            new CsrfProtectionConfig.CsrfInterceptor(tokenManager);

    @Test
    void devePermitirMetodoGetSemToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/home");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertTrue(interceptor.preHandle(request, response, new Object()));
        assertEquals(200, response.getStatus());
    }

    @Test
    void deveBloquearPostSemToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/produtos/salvar");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertFalse(interceptor.preHandle(request, response, new Object()));
        assertEquals(403, response.getStatus());
    }

    @Test
    void devePermitirPutComTokenValido() throws Exception {
        MockHttpSession session = new MockHttpSession();
        String token = tokenManager.getOrCreateToken(session);
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/produtos/1");
        request.setSession(session);
        request.setParameter("_csrf", token);
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertTrue(interceptor.preHandle(request, response, new Object()));
        assertEquals(200, response.getStatus());
    }

    @Test
    void deveBloquearDeleteComTokenAusente() throws Exception {
        MockHttpSession session = new MockHttpSession();
        tokenManager.getOrCreateToken(session);
        MockHttpServletRequest request = new MockHttpServletRequest("DELETE", "/produtos/1");
        request.setSession(session);
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertFalse(interceptor.preHandle(request, response, new Object()));
        assertEquals(403, response.getStatus());
    }
}
