package com.example.gestionmateriel.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
public class AuthUtils {

    public String getCurrentUserId() {
        HttpServletRequest request = getCurrentHttpRequest();
        if (request == null) {
            throw new IllegalStateException("Impossible de récupérer la requête HTTP");
        }

        String userId = request.getHeader("X-User-Id");
        if (userId == null || userId.isEmpty()) {
            throw new IllegalStateException("X-User-Id manquant dans le header");
        }

        return userId;
    }
    public String getCurrentToken() {
        HttpServletRequest request = getCurrentHttpRequest();
        if (request == null) {
            throw new IllegalStateException("Impossible de récupérer la requête HTTP");
        }

        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("Authorization token manquant dans le header");
        }

        return token;
    }


    private HttpServletRequest getCurrentHttpRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        return null;
    }


}
