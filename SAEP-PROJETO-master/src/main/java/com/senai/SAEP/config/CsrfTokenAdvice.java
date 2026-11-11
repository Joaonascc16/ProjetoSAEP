package com.senai.SAEP.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CsrfTokenAdvice {

    private final CsrfTokenManager csrfTokenManager;

    public CsrfTokenAdvice(CsrfTokenManager csrfTokenManager) {
        this.csrfTokenManager = csrfTokenManager;
    }

    @ModelAttribute
    public void adicionarTokenCsrf(Model model, HttpSession session) {
        model.addAttribute("_csrf", csrfTokenManager.getOrCreateToken(session));
    }
}
