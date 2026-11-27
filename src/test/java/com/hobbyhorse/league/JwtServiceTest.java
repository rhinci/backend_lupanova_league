package com.hobbyhorse.league;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.Test;

import com.hobbyhorse.league.model.User;
import com.hobbyhorse.league.security.JwtService;

import io.jsonwebtoken.JwtException;

class JwtServiceTest {

    private static final String SECRET = "test-secret-which-is-at-least-32-bytes-long!!";

    private User anna() {
        User user = new User();
        user.setUsername("anna");
        user.setRole("user");
        return user;
    }

    @Test
    void validTokenReturnsUsername() {
        JwtService service = new JwtService(SECRET, 60);
        String token = service.generate(anna());
        System.out.println("Токен: " + token);
        assertEquals("anna", service.parseUsername(token));
    }

    @Test
    void forgedPayloadIsRejected() {
        JwtService service = new JwtService(SECRET, 60);
        String[] parts = service.generate(anna()).split("\\.");
        String fakePayload = Base64.getUrlEncoder().withoutPadding().encodeToString(
                "{\"sub\":\"admin\",\"exp\":9999999999}".getBytes(StandardCharsets.UTF_8));
        String forged = parts[0] + "." + fakePayload + "." + parts[2];
        assertThrows(JwtException.class, () -> service.parseUsername(forged));
    }

    @Test
    void tokenSignedWithOtherSecretIsRejected() {
        JwtService other = new JwtService("another-secret-which-is-also-32-bytes-long!", 60);
        JwtService service = new JwtService(SECRET, 60);
        String token = other.generate(anna());
        assertThrows(JwtException.class, () -> service.parseUsername(token));
    }

    @Test
    void expiredTokenIsRejected() {
        JwtService service = new JwtService(SECRET, -1);
        String token = service.generate(anna());
        assertThrows(JwtException.class, () -> service.parseUsername(token));
    }
}