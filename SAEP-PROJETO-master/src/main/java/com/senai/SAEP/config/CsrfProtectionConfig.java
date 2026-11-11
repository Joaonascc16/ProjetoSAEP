package com.senai.SAEP.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;

@Configuration
public class CsrfProtectionConfig implements WebMvcConfigurer {

    private final CsrfTokenManager csrfTokenManager;

    public CsrfProtectionConfig(CsrfTokenManager csrfTokenManager) {
        this.csrfTokenManager = csrfTokenManager;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CsrfInterceptor(csrfTokenManager));
    }

    private static class CsrfInterceptor implements HandlerInterceptor {

        private final CsrfTokenManager csrfTokenManager;

        private CsrfInterceptor(CsrfTokenManager csrfTokenManager) {
            this.csrfTokenManager = csrfTokenManager;
        }

        @Override
        public boolean preHandle(
                HttpServletRequest request,
                HttpServletResponse response,
                Object handler) throws IOException {

            if (!"POST".equalsIgnoreCase(request.getMethod())) {
                return true;
            }

            HttpSession session = request.getSession(false);
            String submittedToken = request.getParameter("_csrf");

            if (!csrfTokenManager.isValid(session, submittedToken)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token CSRF inválido ou ausente.");
                return false;
            }

            return true;
        }
    }
}
