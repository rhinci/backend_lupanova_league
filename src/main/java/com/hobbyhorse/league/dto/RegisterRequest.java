package com.hobbyhorse.league.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Логин обязателен")
        @Size(min = 3, max = 50, message = "Логин от 3 до 50 символов")
        String username,

        @NotBlank(message = "Пароль обязателен")
        @Size(min = 6, max = 72, message = "Пароль от 6 до 72 символов")
        String password) {
}