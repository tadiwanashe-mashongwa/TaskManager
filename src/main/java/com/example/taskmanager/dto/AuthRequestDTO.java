package com.example.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;

public record AuthRequestDTO(

    @NotBlank(message = "name required")
    @Size(message = "size should be 3 to 20",min = 3,max = 20)
    String username,

    @NotBlank(message="email required")
     String email,

    @NotBlank(message = "password is required")
    @Size(min = 8)
    String password
)
{};
