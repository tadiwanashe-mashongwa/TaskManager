package com.example.taskmanager.dto;

import java.time.Instant;

public record LoginResponseDTO(String token,long expirationTime,Instant issuedAt)
{
};
