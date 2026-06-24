package com.example.taskmanager.exception;

import com.example.taskmanager.controller.AuthController;
import com.example.taskmanager.dto.AuthRequestDTO;

import com.example.taskmanager.dto.LoginRequestDTO;
import com.example.taskmanager.service.AuthService;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.time.Instant;

import java.util.Map;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class GlobalExceptionHandlerTest {
    @Autowired
    private  MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void handleConflict_shouldReturn409AndErrorResponseDTO() throws Exception{
        //Act
        Long id=1L;
        AuthRequestDTO authRequestDTO=new AuthRequestDTO(
                "mukundi",
                "example@gmail.com",
                "password123"
        );
        when(authService.createUser(authRequestDTO)).thenThrow(new ResourceConflictException("user "+authRequestDTO.email()+" exists"));


        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("conflict"))
                .andExpect(jsonPath("$.message").value("user example@gmail.com exists"))
                .andExpect(jsonPath("$.path").value("=/api/auth"))
                .andExpect(jsonPath("$.validationErrors").isEmpty());

        verify(authService).createUser(any(AuthRequestDTO.class));
    }
    @ParameterizedTest(name = "should return 404 Not Found for HTTP method: {0}")
    @ValueSource(strings = {"GET","DELETE","PUT"})
    void handleNotFound_shouldReturn404andErrorResponseDTO(String httpMethod) throws Exception{
        //Act
        Long nonExistentId=230L;
        String exceptionMessage = "Task not found with id: " + nonExistentId;
        String targetUri = "/api/auth/" + nonExistentId;

        AuthRequestDTO authRequestDTO=new AuthRequestDTO("mukundi","example@gmail.com","password123");

        when(authService.findUserById(anyLong())).thenThrow(new ResourceNotFoundException(exceptionMessage));
        when(authService.updateUser(anyLong(), any())).thenThrow(new ResourceNotFoundException(exceptionMessage));
        doThrow(new ResourceNotFoundException(exceptionMessage)).when(authService).deleteUserById(nonExistentId);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .request(httpMethod, URI.create(targetUri))
                .contentType(MediaType.APPLICATION_JSON);
        if ("PUT".equals(httpMethod)) {
            AuthRequestDTO updatePayload = new AuthRequestDTO("updatedName", "test@gmail.com", "password123");
            requestBuilder.content(objectMapper.writeValueAsString(updatePayload));
        }
        // Assert: Verify that your GlobalExceptionHandler maps the response identically for all endpoints
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource Not Found"))
                .andExpect(jsonPath("$.message").value(exceptionMessage))
                .andExpect(jsonPath("$.path").value("="+targetUri)) // Matches your exact HttpServletRequest URI fix!
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.validationErrors").isEmpty());

        if ("GET".equals(httpMethod)) {
            verify(authService).findUserById(anyLong());
        } else if ("DELETE".equals(httpMethod)) {
            verify(authService).deleteUserById(nonExistentId);

        } else if ("PUT".equals(httpMethod)) {
            verify(authService).updateUser(eq(nonExistentId), any());

        }
    }

    @Test
    void handleValidationErrors_shouldReportError_whenUsernameIsBlank() throws Exception {
        // Arrange: Missing username
        String invalidPayload = "{\"username\":\"\",\"email\":\"example@gmail.com\",\"password\":\"password123\"}";

        // Act & Assert
        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.username").exists()) // Targets username specifically
                .andExpect(jsonPath("$.validationErrors.email").doesNotExist());

        verifyNoInteractions(authService);
    }

    @Test
    void handleValidationErrors_shouldReportError_whenEmailIsMalformed() throws Exception {
        // Arrange: Malformed email string
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("invalid email", "password123");
        // Act & Assert
        when(authService.loginUser(loginRequestDTO.email(),loginRequestDTO.password())).thenThrow(new IllegalArgumentException("Invalid email or password credential"));
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.email").exists()) // Targets email specifically
                .andExpect(jsonPath("$.validationErrors.username").doesNotExist());

        verifyNoInteractions(authService);
    }

    @Test
    void handleValidationErrors_shouldReportError_whenPasswordIsBlank() throws Exception {
        // Arrange: Missing password
        String invalidPayload = "{\"username\":\"mukundi\",\"email\":\"example@gmail.com\",\"password\":\"\"}";

        // Act & Assert
        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.password").exists()) // Targets password specifically
                .andExpect(jsonPath("$.validationErrors.username").doesNotExist());

        verifyNoInteractions(authService);
    }
    @Test
    void handleInvalidCredentuials_shouldReportErrorWhenPasswordOrEmailIsInvalid() throws Exception{

        // Arrange: Missing password
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("example@gmail.com", "wrongpassword");
        String targetUri = "/api/auth/login";
        when(authService.loginUser(loginRequestDTO.email(), loginRequestDTO.password()))
                .thenThrow(new BadCredentialsException("Invalid email or password credentials"));

        // Act & Assert
        mockMvc.perform(post(targetUri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid email or password credentials"))
                .andExpect(jsonPath("$.path").value("="+targetUri)) // Matches your exact HttpServletRequest URI fix!
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.validationErrors").isEmpty());


        verify(authService).loginUser(loginRequestDTO.email(), loginRequestDTO.password());

    }
}
