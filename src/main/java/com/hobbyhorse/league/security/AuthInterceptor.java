package com.hobbyhorse.league.security;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

import com.hobbyhorse.league.model.User;
import com.hobbyhorse.league.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String CURRENT_USER = "currentUser";

    private static final Set<String> READ_METHODS = Set.of("GET", "HEAD", "OPTIONS");
    private static final Set<String> PUBLIC_PATHS = Set.of("/auth/register", "/auth/login");
    

    private final AuthService authService;

    public AuthInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        String header = request.getHeader("Authorization");
        User user = null;
        if (header != null) {
            user = authService.authenticate(header);
            request.setAttribute(CURRENT_USER, user);
        }

        boolean readOnly = READ_METHODS.contains(request.getMethod());
        boolean publicPath = PUBLIC_PATHS.contains(request.getRequestURI());

        if (user == null && !readOnly && !publicPath) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Требуется вход");
        }
        return true;
    }
}