package com.example.taskmanager.controller;

import com.example.taskmanager.dto.ApiResponse;
import com.example.taskmanager.service.StatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
public class StatusController {

    private final StatusService statusService;

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> getHealth() {
        String healthStatus = statusService.health();
        if (healthStatus.startsWith("DOWN")) {
            // If the deep check fails, return an HTTP 503 Service Unavailable instantly
            return ResponseEntity.status(503).body(ApiResponse.error("System health compromised: " + healthStatus));
        }
        return ResponseEntity.ok(ApiResponse.success("System operational", healthStatus));
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, String>>> getInfo() {
        return ResponseEntity.ok(ApiResponse.success("Metadata metadata compiled", statusService.info()));
    }

    @GetMapping("/echo")
    public ResponseEntity<ApiResponse<String>> getEcho(@RequestParam String input) {
        return ResponseEntity.ok(ApiResponse.success("Payload echoed successfully", statusService.echo(input)));
    }
}