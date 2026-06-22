package com.example.taskmanager.dto;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

public record ErrorResponseDTO(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> validationErrors // Holds: {"field_name": "error_reason"}
) {
    // Compact constructor to allow clean creation of standard errors without field logs
    public ErrorResponseDTO(Instant timestamp, int status, String error, String message, String path) {
        this(timestamp, status, error, message, path, Collections.emptyMap());
    }
}