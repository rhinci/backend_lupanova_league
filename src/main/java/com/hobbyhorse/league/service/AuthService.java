package com.hobbyhorse.league.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hobbyhorse.league.model.User;
import com.hobbyhorse.league.repository.UserRepository;
import com.hobbyhorse.league.security.JwtService;
import com.hobbyhorse.league.security.LoginRateLimiter;

import io.jsonwebtoken.JwtException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final LoginRateLimiter loginRateLimiter;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       LoginRateLimiter loginRateLimiter) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.loginRateLimiter = loginRateLimiter;
    }

    public User register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw conflict();
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole("user");

        try {
            return userRepository.save(user);
        } catch (DuplicateKeyException e) {
            throw conflict();
        }
    }

    public String login(String username, String password) {
        return jwtService.generate(verify(username, password));
    }

    public User authenticate(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return authenticateBearer(header.substring(7).trim());
        }
        return authenticateBasic(header);
    }

    private User authenticateBearer(String token) {
        String username;
        try {
            username = jwtService.parseUsername(token);
        } catch (JwtException | IllegalArgumentException e) {
            throw invalidToken();
        }
        return userRepository.findByUsername(username)
                .orElseThrow(this::invalidToken);
    }

    private User authenticateBasic(String header) {
        if (header == null || !header.startsWith("Basic ")) {
            throw unauthorized();
        }

        String decoded;
        try {
            byte[] bytes = Base64.getDecoder().decode(header.substring(6).trim());
            decoded = new String(bytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            throw unauthorized();
        }

        int colon = decoded.indexOf(':');
        if (colon < 0) {
            throw unauthorized();
        }
        return verify(decoded.substring(0, colon), decoded.substring(colon + 1));
    }

    private User verify(String username, String password) {
        loginRateLimiter.checkAllowed(username);

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            loginRateLimiter.recordFailure(username);
            throw unauthorized();
        }

        loginRateLimiter.reset(username);
        return user;
    }

    private ResponseStatusException conflict() {
        return new ResponseStatusException(HttpStatus.CONFLICT, "Логин уже занят");
    }

    private ResponseStatusException unauthorized() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверный логин или пароль");
    }

    private ResponseStatusException invalidToken() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Недействительный или просроченный токен");
    }
}