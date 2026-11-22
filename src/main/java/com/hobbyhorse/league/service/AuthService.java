package com.hobbyhorse.league.service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.hobbyhorse.league.model.User;
import com.hobbyhorse.league.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

    public User authenticate(String header) {
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
        String username = decoded.substring(0, colon);
        String password = decoded.substring(colon + 1);

        User user = userRepository.findByUsername(username)
                .orElseThrow(this::unauthorized);
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw unauthorized();
        }
        return user;
    }

    private ResponseStatusException unauthorized() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверный логин или пароль");
    }

    private ResponseStatusException conflict() {
        return new ResponseStatusException(HttpStatus.CONFLICT, "Логин уже занят");
    }
}