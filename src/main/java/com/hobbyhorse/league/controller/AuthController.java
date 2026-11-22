package com.hobbyhorse.league.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hobbyhorse.league.dto.RegisterRequest;
import com.hobbyhorse.league.dto.UserResponse;
import com.hobbyhorse.league.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

import com.hobbyhorse.league.model.User;
import com.hobbyhorse.league.security.AuthInterceptor;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return UserResponse.from(authService.register(request.username(), request.password()));
    }
    
    @GetMapping("/me")
    public UserResponse me(HttpServletRequest request) {
        User user = (User) request.getAttribute(AuthInterceptor.CURRENT_USER);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Требуется вход");
        }
        return UserResponse.from(user);
    }
}