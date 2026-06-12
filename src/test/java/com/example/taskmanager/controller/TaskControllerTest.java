package com.example.taskmanager.controller;


import com.example.taskmanager.dto.TaskRequestDTO;
import com.example.taskmanager.dto.TaskResponseDTO;
import com.example.taskmanager.entity.Status;
import com.example.taskmanager.service.TaskService;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
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
    void creatTask_shouldReturn201AndTaskResponseDTO() throws Exception{

        //Assert
        Instant timeMarker = Instant.parse("2026-01-10T00:00:00Z");
        TaskRequestDTO requestDTO = new TaskRequestDTO("cook", "cook a hot meal", Status.ACTIVE, timeMarker);
        TaskResponseDTO responseDTO = new TaskResponseDTO(1L, "cook", "cook a hot meal", Status.ACTIVE, timeMarker, timeMarker, timeMarker);

        when(taskService.createTask(requestDTO)).thenReturn(responseDTO);

        //Act and Assert
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated()) // Asserts HTTP Status is 201 Created
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("cook"))
                .andExpect(jsonPath("$.description").value("cook a hot meal"));

        verify(taskService).createTask(any(TaskRequestDTO.class));

    }

    @Test
    void findTaskById_shouldReturn200AndTaskResponseDTO () throws Exception{
        //Assert
        Instant timeMarker = Instant.parse("2026-01-10T00:00:00Z");
        Long id=1L;
        TaskResponseDTO responseDTO = new TaskResponseDTO(1L, "cook", "cook a hot meal", Status.ACTIVE, timeMarker, timeMarker, timeMarker);

        when(taskService.findTaskById(id)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/tasks/{id}", id)) // 💡 Using correct lowercase 'get' request builder
                .andExpect(status().isOk()) // 💡 Fetching an existing task must yield an HTTP 200 OK status
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("cook"))
                .andExpect(jsonPath("$.description").value("cook a hot meal"));

        // Verify the controller actually triggered the service layer dependency
        verify(taskService).findTaskById(id);
    }

    @Test
    void getAllTasks_shouldReturn200AllTasksorEmptyList() throws Exception{
        //Assert
        Instant timeMarker = Instant.parse("2026-01-10T00:00:00Z");
       ;
        TaskResponseDTO responseDTO = new TaskResponseDTO(1L, "cook", "cook a hot meal", Status.ACTIVE, timeMarker, timeMarker, timeMarker);
        when(taskService.findAllTasks()).thenReturn(List.of(
          responseDTO,responseDTO,responseDTO
        ));

        mockMvc.perform(get("/api/tasks")).
                andExpect(status().isOk()).
                andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].title").value("cook"))
                .andExpect(jsonPath("$[2].description").value("cook a hot meal"));

        verify(taskService).findAllTasks();
    }

    @Test
    void deleteTaskById_shouldReturn200() throws Exception{
        //Assert
        Long id=1L;

        mockMvc.perform(delete("/api/tasks/{id}",id))
                        .andExpect(status().is2xxSuccessful());

        verify(taskService).deleteTaskById(id);
    }

    @Test
    void updateTask_shouldUpdateTaskandReturn200andNewTask() throws Exception{
        //Assert
        Instant timeMarker = Instant.parse("2026-01-10T00:00:00Z");
        TaskRequestDTO requestDTO = new TaskRequestDTO("cook", "cook a hot meal", Status.ACTIVE, timeMarker);
        TaskResponseDTO responseDTO = new TaskResponseDTO(1L, "cook", "cook a hot meal", Status.ACTIVE, timeMarker, timeMarker, timeMarker);
        Long id=1L;


        when(taskService.updateTask(id,requestDTO)).thenReturn(responseDTO);


        //Act and Assert
        mockMvc.perform(put("/api/tasks/{id}",id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk()) // Asserts HTTP Status is 201 Created
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("cook"))
                .andExpect(jsonPath("$.description").value("cook a hot meal"));

        verify(taskService).updateTask(id,requestDTO);
    }
}
