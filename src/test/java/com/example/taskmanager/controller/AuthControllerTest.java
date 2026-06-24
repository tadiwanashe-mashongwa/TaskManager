package com.example.taskmanager.controller;

import com.example.taskmanager.dto.AuthRequestDTO;
import com.example.taskmanager.dto.AuthResponseDTO;
import com.example.taskmanager.dto.LoginRequestDTO;
import com.example.taskmanager.dto.LoginResponseDTO;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.service.AuthService;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.function.RequestPredicates.GET;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    void createNewUser_shouldReturnAuthResponseDTOAnd201() throws Exception { // Note: mockMvc throws Exception
        // Arrange
        Instant timeMarker = Instant.parse("2026-01-10T00:00:00Z");
        AuthRequestDTO requestDTO = new AuthRequestDTO("mukundi", "example@gmail.com", "password123");
        AuthResponseDTO responseDTO = new AuthResponseDTO(1L, "mukundi", "example@gmail.com", timeMarker);

        // FIXED: Using any() argument matcher because serialization alters the object instance reference
        when(authService.createUser(any(AuthRequestDTO.class))).thenReturn(responseDTO);

        // Act and Assert
        mockMvc.perform(post("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("mukundi"))
                .andExpect(jsonPath("$.email").value("example@gmail.com"));

        verify(authService).createUser(any(AuthRequestDTO.class));
    }

    @Test
    void getAllUsers_shouldReturn200AndAllAvailableUsers() throws Exception {
        // Arrange
        Instant timeMarker = Instant.parse("2026-01-10T00:00:00Z");
        AuthResponseDTO responseDTO = new AuthResponseDTO(1L, "mukundi", "example@gmail.com", timeMarker);
        AuthResponseDTO responseDTO1 = new AuthResponseDTO(2L, "john", "john@gmail.com", timeMarker);

        when(authService.findAllUsers()).thenReturn(List.of(responseDTO, responseDTO1));

        // Act and Assert
        mockMvc.perform(get("/api/auth"))
                .andExpect(status().isOk())
                // FIXED: Standard bracket array index syntax utilized to parse collection states safely
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("mukundi"))
                .andExpect(jsonPath("$[0].email").value("example@gmail.com"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].username").value("john"))
                .andExpect(jsonPath("$[1].email").value("john@gmail.com"));

        verify(authService).findAllUsers();
    }
    @Test
    void updateUser_shouldUpdateAnExistingUserAndReturn200AndAuthResponseDTO() throws Exception{
        // Arrange
        Long id=1L;
        Instant timeMarker = Instant.parse("2026-01-10T00:00:00Z");
        AuthRequestDTO requestDTO = new AuthRequestDTO("mukundi", "example@gmail.com", "password123");
        AuthResponseDTO responseDTO = new AuthResponseDTO(1L, "mukundi", "example@gmail.com", timeMarker);
        // FIXED: Using any() argument matcher because serialization alters the object instance reference
        when(authService.updateUser(1L,requestDTO)).thenReturn(responseDTO);

        // Act and Assert
        mockMvc.perform(put("/api/auth/{id}",id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("mukundi"))
                .andExpect(jsonPath("$.email").value("example@gmail.com"));

    verify(authService).updateUser(eq(1L),any(AuthRequestDTO.class));

    }

    @Test
    void deleteAUser_shouldDeleteUserAndReturn204() throws Exception{
        // Arrange
        Long id=1L;

        // FIXED: Using any() argument matcher because serialization alters the object instance reference
       //authService.deleteUserById(id);

        // Act and Assert
        mockMvc.perform(delete("/api/auth/{id}",id))
                .andExpect(status().isNoContent());
                // FIXED: Standard bracket array index syntax utilized to parse collection states safely

        verify(authService).deleteUserById(eq(id));
    }
    @Test
    void findUserById_shouldReturnAuthResponseDTOAnd200() throws Exception{
        Long id=1L;
        Instant timeMarker = Instant.parse("2026-01-10T00:00:00Z");
        AuthResponseDTO authResponseDTO=new AuthResponseDTO(id,"mukundi","example@gmail.com",timeMarker);

        when(authService.findUserById(id)).thenReturn(authResponseDTO);

        mockMvc.perform(get("/api/auth/{id}",id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.username").value("mukundi"))
                .andExpect(jsonPath("$.email").value("example@gmail.com"))
                .andExpect(jsonPath("$.createdAt").value("2026-01-10T00:00:00Z"));
        verify(authService).findUserById(eq(id));
    }
    @Test
    void login_shouldReturn200AndLoginResponse() throws Exception{
        //Arrange
        String email="example@gmail.com";
        String password="password123";
        Instant timeMarker = Instant.parse("2026-01-10T00:00:00Z");
        LoginResponseDTO loginResponseDTO=new LoginResponseDTO("token",360000,timeMarker);
        LoginRequestDTO loginRequestDTO=new LoginRequestDTO(email,password);
        when(authService.loginUser(email,password)).thenReturn(loginResponseDTO);

        //Act
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDTO))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"))
                .andExpect(jsonPath("$.expirationTime").value(360000))
                .andExpect(jsonPath("$.issuedAt").value("2026-01-10T00:00:00Z"));
        verify(authService).loginUser(email,password);
    }
    }
