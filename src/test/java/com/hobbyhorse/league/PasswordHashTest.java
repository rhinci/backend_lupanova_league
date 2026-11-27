package com.hobbyhorse.league;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class PasswordHashTest {

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void hashingWorks() {
        String hash1 = encoder.encode("qwerty123");
        String hash2 = encoder.encode("qwerty123");

        System.out.println("Хэш 1: " + hash1);
        System.out.println("Хэш 2: " + hash2);

        assertNotEquals(hash1, hash2);                   
        assertTrue(encoder.matches("qwerty123", hash1)); 
        assertFalse(encoder.matches("qwerty124", hash1)); 
    }
}