package com.example.taskmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "email required")
        @Email(message = "invalid email")
        String email,

        @NotBlank(message = "password is required")
        String password
) {}