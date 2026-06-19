package com.example.taskmanager.dto;

import com.example.taskmanager.entity.Status;

import java.time.Instant;

public record TaskResponseDTO(
        Long id,
        String title,
        String description,
        Status status,
        Instant dueDate,
        Instant createdAt,
        Instant updatedAt

) {

}
