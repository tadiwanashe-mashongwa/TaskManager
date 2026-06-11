package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskRequestDTO;
import com.example.taskmanager.dto.TaskResponseDTO;
import com.example.taskmanager.entity.Status;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_shouldSaveTaskAndReturnSavedTask() {
        // Arrange
        Instant dueDate = Instant.parse("2026-01-10T00:00:00Z");
        Instant createdAt = Instant.parse("2026-01-10T00:00:00Z");
        Instant updatedAt = Instant.parse("2026-01-10T00:00:00Z");

        TaskRequestDTO taskRequestDTO = new TaskRequestDTO(
                "cook",
                "cook a hot meal",
                Status.ACTIVE,
                dueDate
        );

        Task savedTask = new Task(
                1L,
                taskRequestDTO.title(),
                taskRequestDTO.description(),
                taskRequestDTO.status(),
                taskRequestDTO.dueDate(),
                createdAt,
                updatedAt
        );

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Act
        TaskResponseDTO createdTask = taskService.createTask(taskRequestDTO);
        //Assert
        assertEquals(savedTask.getId(),createdTask.id());
        assertEquals(savedTask.getTitle(),createdTask.title());
        assertEquals(savedTask.getDescription(),createdTask.description());
        assertEquals(savedTask.getStatus(),createdTask.status());
        assertEquals(savedTask.getDueDate(),createdTask.dueDate());
        assertEquals(savedTask.getCreatedAt(),createdTask.createdAt());
        assertEquals(savedTask.getUpdatedAt(),createdTask.updatedAt());

        verify(taskRepository).save(any(Task.class));
    }}