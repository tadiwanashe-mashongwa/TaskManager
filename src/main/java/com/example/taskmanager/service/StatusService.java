package com.example.taskmanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j // 💡 Gives us a professional enterprise logging framework engine instance
public class StatusService {

    private final DataSource dataSource; // Injecting the H2 database engine pool directly

    // 💡 Injecting properties dynamically from application.properties
    @Value("${app.metadata.name}")
    private String appName;

    @Value("${app.metadata.version}")
    private String appVersion;

    @Value("${app.metadata.environment}")
    private String appEnv;

    /**
     * Executes a Deep Health Check by actively validating downstream database availability.
     */
    public String health() {
        // Run a low-overhead query against H2 to confirm connection pool viability
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("SELECT 1"); // The standard universal database ping query
            return "UP";

        } catch (Exception e) {
            log.error("🔴 CRITICAL DIAGNOSTIC FAILURE: Database connectivity has collapsed!", e);
            return "DOWN - Database Unavailable";
        }
    }

    /**
     * Aggregates decoupled environmental system metadata.
     */
    public Map<String, String> info() {
        return Map.of(
                "applicationName", appName,
                "version", appVersion,
                "environment", appEnv,
                "javaVersion", System.getProperty("java.version")
        );
    }

    /**
     * Sanitizes and echoes an input string back safely.
     */
    public String echo(String input) {
        if (input == null || input.isBlank()) {
            return "Echo: Nothing to say.";
        }

        // 🛡️ Basic sanitization defense: strip out newline characters to block Log Injection attacks
        String sanitized = input.replace("\n", "").replace("\r", "");
        log.info("System diagnostic echo executed for payload size: {} chars", sanitized.length());

        return "Echo: " + sanitized;
    }
}