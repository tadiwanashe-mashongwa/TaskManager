package com.example.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import java.time.Instant;

@Getter // 💡 Exposes standard getters so Jackson can serialize the fields to JSON automatically
public class ApiResponse<T> {

    private final String status;      // e.g., "SUCCESS", "ERROR"
    private final String message;     // Human-readable summary of the operation

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private final Instant timestamp;  // High-precision UTC timestamp marker

    private final T data;             // The actual payload (can be a Task, a List, or null)

    // Private constructor to force instantiation strictly through our clean static factory methods
    private ApiResponse(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.timestamp = Instant.now(); // Automatically logs the exact moment the payload wraps
        this.data = data;
    }

    // 💡 Static Factory Method for Successful Operations with Data
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("SUCCESS", message, data);
    }

    // 💡 Static Factory Method for Successful Operations without Data (e.g., Deletions)
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>("SUCCESS", message, null);
    }

    // 💡 Static Factory Method for Failure Operations
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("ERROR", message, null);
    }
}