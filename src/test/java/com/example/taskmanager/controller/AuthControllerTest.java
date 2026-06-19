package com.example.taskmanager.controller;

import com.example.taskmanager.dto.AuthRequestDTO;
import com.example.taskmanager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.security.cert.Extension;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @Test
    void createNewUser_shouldReturnAuthResponseDTOAnd201(){
      //Assert
        AuthRequestDTO authRequestDTO=new AuthRequestDTO("mukundi","example@gmail.com","password123");

    }
}
