package com.hobbyhorse.league.repository;

import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.hobbyhorse.league.model.User;

@Repository
public class UserRepository {

    private static final RowMapper<User> USER_MAPPER = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        return user;
    };

    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public User save(User user) {
        Long id = jdbc.queryForObject(
                "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?) RETURNING id",
                Long.class,
                user.getUsername(), user.getPasswordHash(), user.getRole());
        user.setId(id);
        return user;
    }

    public Optional<User> findByUsername(String username) {
        return jdbc.query(
                "SELECT id, username, password_hash, role FROM users WHERE username = ?",
                USER_MAPPER, username)
                .stream()
                .findFirst();
    }

    public void saveAdmin(String username, String passwordHash) {
        jdbc.update(
                "INSERT INTO users (username, password_hash, role) VALUES (?, ?, 'admin') "
                        + "ON CONFLICT (username) DO UPDATE "
                        + "SET password_hash = EXCLUDED.password_hash, role = 'admin'",
                username, passwordHash);
    }
}