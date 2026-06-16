package com.example.taskmanager.controller;


import com.example.taskmanager.dto.TaskRequestDTO;
import com.example.taskmanager.dto.TaskResponseDTO;
import com.example.taskmanager.entity.Status;
import com.example.taskmanager.service.TaskService;


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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;



@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @Test
    void creatTask_shouldReturn201AndTaskResponseDTO() throws Exception {
        // Arrange
        Instant timeMarker = Instant.parse("2026-01-10T00:00:00Z");
        TaskRequestDTO requestDTO = new TaskRequestDTO("cook", "cook a hot meal", Status.ACTIVE, timeMarker);
        TaskResponseDTO responseDTO = new TaskResponseDTO(1L, "cook", "cook a hot meal", Status.ACTIVE, timeMarker, timeMarker, timeMarker);

        when(taskService.createTask(requestDTO)).thenReturn(responseDTO);

        // Act and Assert
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                // 💡 UPDATED: Verifying global envelope metadata properties
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Task created successfully"))
                .andExpect(jsonPath("$.timestamp").exists())
                // 💡 UPDATED: Drilled deep into the nested $.data slot to find payload details
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.title").value("cook"))
                .andExpect(jsonPath("$.data.description").value("cook a hot meal"));

        verify(taskService).createTask(any(TaskRequestDTO.class));
    }

    @Test
    void findTaskById_shouldReturn200AndTaskResponseDTO() throws Exception {
        // Arrange
        Instant timeMarker = Instant.parse("2026-01-10T00:00:00Z");
        Long id = 1L;
        TaskResponseDTO responseDTO = new TaskResponseDTO(1L, "cook", "cook a hot meal", Status.ACTIVE, timeMarker, timeMarker, timeMarker);

        when(taskService.findTaskById(id)).thenReturn(responseDTO);

        // Act and Assert
        mockMvc.perform(get("/api/tasks/{id}", id))
                .andExpect(status().isOk())
                // 💡 UPDATED: Verifying envelope shell properties
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Task retrieved successfully"))
                // 💡 UPDATED: Navigating inside the single object generic type container
                .andExpect(jsonPath("$.data.id").value(id))
                .andExpect(jsonPath("$.data.title").value("cook"))
                .andExpect(jsonPath("$.data.description").value("cook a hot meal"));

        verify(taskService).findTaskById(id);
    }

    @Test
    void getAllTasks_shouldReturn200AllTasksorEmptyList() throws Exception {
        // Arrange
        Instant timeMarker = Instant.parse("2026-01-10T00:00:00Z");
        TaskResponseDTO responseDTO = new TaskResponseDTO(1L, "cook", "cook a hot meal", Status.ACTIVE, timeMarker, timeMarker, timeMarker);

        when(taskService.findAllTasks()).thenReturn(List.of(
                responseDTO, responseDTO, responseDTO
        ));

        // Act and Assert
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                // 💡 UPDATED: Enforcing the corrected string message contract
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("All tasks retrieved successfully"))
                // 💡 UPDATED: Navigating into the data payload which is now an array nested under $.data
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0].title").value("cook"))
                .andExpect(jsonPath("$.data[2].description").value("cook a hot meal"));

        verify(taskService).findAllTasks();
    }

    @Test
    void deleteTaskById_shouldReturn200() throws Exception {
        // Arrange
        Long id = 1L;

        // Act and Assert
        mockMvc.perform(delete("/api/tasks/{id}", id))
                .andExpect(status().isOk()) // 💡 UPDATED: Enforcing 200 OK block since we return an ApiResponse
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Task deleted successfully"))
                .andExpect(jsonPath("$.data").isEmpty()); // 💡 Asserting that the Void payload container holds no internal elements

        verify(taskService).deleteTaskById(id);
    }

    @Test
    void updateTask_shouldUpdateTaskandReturn200andNewTask() throws Exception {
        // Arrange
        Instant timeMarker = Instant.parse("2026-01-10T00:00:00Z");
        TaskRequestDTO requestDTO = new TaskRequestDTO("cook", "cook a hot meal", Status.ACTIVE, timeMarker);
        TaskResponseDTO responseDTO = new TaskResponseDTO(1L, "cook", "cook a hot meal", Status.ACTIVE, timeMarker, timeMarker, timeMarker);
        Long id = 1L;

        when(taskService.updateTask(id, requestDTO)).thenReturn(responseDTO);

        // Act and Assert
        mockMvc.perform(put("/api/tasks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                // 💡 UPDATED: Asserting the payload envelope structure for our Update transaction mapping
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Task updated successfully"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.title").value("cook"))
                .andExpect(jsonPath("$.data.description").value("cook a hot meal"));

        verify(taskService).updateTask(id, requestDTO);
    }
}