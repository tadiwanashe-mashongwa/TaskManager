package com.example.taskmanager.dto;

import com.example.taskmanager.entity.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record TaskRequestDTO(
        @NotBlank(message="title cannot be blank")
        @Size(min = 3,max = 20,message = "size should be between 3 and 20")
        String title,

        @NotBlank(message="description cannot be blank")
        @Size(min = 3,max = 50,message = "size should be between 3 and 50")
        String description,

        @NotNull(message = "status required")
        Status status,

        @NotNull(message = "due date required")
        Instant dueDate
) {
};
