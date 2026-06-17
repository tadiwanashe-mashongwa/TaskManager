package com.example.taskmanager.dto;

import java.time.Instant;

public record AuthResponseDTO(
        Long id,
        String username,
        String email,
        Instant createdAt
) {
};
