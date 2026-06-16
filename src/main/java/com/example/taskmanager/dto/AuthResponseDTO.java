package com.example.taskmanager.dto;

import java.time.Instant;

public record AuthResponseDTO(
        String username,
        String email,
        Instant createdAt
) {
};
