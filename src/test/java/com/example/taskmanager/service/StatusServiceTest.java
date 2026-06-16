package com.example.taskmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // 💡 Bootstraps Mockito for fast, zero-context unit testing
public class StatusServiceTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private Statement statement;

    private StatusService statusService;

    @BeforeEach
    void setUp() {
        // Instantiate the service using the mocked DataSource dependency
        statusService = new StatusService(dataSource);

        // 💡 ReflectionTestUtils allows us to safely inject private @Value fields without spinning up Spring Boot
        ReflectionTestUtils.setField(statusService, "appName", "Test Engine");
        ReflectionTestUtils.setField(statusService, "appVersion", "1.0.0");
        ReflectionTestUtils.setField(statusService, "appEnv", "Test-Env");
    }

    @Test
    void health_shouldReturnUp_WhenDatabaseIsResponsive() throws Exception {
        // Arrange - Stub the nested JDBC network objects to mimic a successful query run
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.execute("SELECT 1")).thenReturn(true);

        // Act
        String result = statusService.health();

        // Assert
        assertEquals("UP", result);
        verify(statement, times(1)).execute("SELECT 1");
    }

    @Test
    void health_shouldReturnDown_WhenDatabaseThrowsException() throws Exception {
        // Arrange - Force the connection pool to throw a severe connection failure exception
        when(dataSource.getConnection()).thenThrow(new SQLException("Connection refused by H2 host database"));

        // Act
        String result = statusService.health();

        // Assert
        assertTrue(result.contains("DOWN"));
        assertTrue(result.contains("Database Unavailable"));
    }

    @Test
    void info_shouldReturnPopulatedMetadataMap() {
        // Act
        Map<String, String> infoMap = statusService.info();

        // Assert
        assertEquals("Test Engine", infoMap.get("applicationName"));
        assertEquals("1.0.0", infoMap.get("version"));
        assertEquals("Test-Env", infoMap.get("environment"));
        assertNotNull(infoMap.get("javaVersion"));
    }

    @Test
    void echo_shouldScrubNewlinesAndCarriageReturns_ToBlockLogInjection() {
        // Arrange - Input contains dangerous carriage return and newline forging payloads
        String maliciousInput = "CleanText\r\n[INFO] Forged Log Entry Line\nMoreText";

        // Act
        String result = statusService.echo(maliciousInput);

        // Assert
        // The service layer must strip out the characters, forcing the string flat on a single line
        assertEquals("Echo: CleanText[INFO] Forged Log Entry LineMoreText", result);
    }
}