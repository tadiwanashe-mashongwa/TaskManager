/* package com.example.taskmanager.service;

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
import java.util.List;
import java.util.Optional;

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

        verify(taskRepository).save(any(Task.class));}
    @Test
    void findTaskById_shouldReturnTskWhenIdExists(){
        //Arrange
        Instant dueDate = Instant.parse("2026-01-10T00:00:00Z");
        Instant createdAt = Instant.parse("2026-01-10T00:00:00Z");
        Instant updatedAt = Instant.parse("2026-01-10T00:00:00Z");
        Long id=10L;

        when(taskRepository.findById(id)).thenReturn(
                Optional.of(new Task(
                        10L,
                        "cook",
                        "cook a hot meal",
                        Status.ACTIVE,
                        dueDate,
                        createdAt,
                        updatedAt
                ))
        );
        //Act
        TaskResponseDTO taskResponseDTO= taskService.findTaskById(id);
        //Assert
        assertEquals(taskResponseDTO.id(),id);
        verify(taskRepository).findById(id);
    }
    @Test
    void getAllTasks_shouldReturnAllPresentTasks(){
        //Arrange
        Instant dueDate = Instant.parse("2026-01-10T00:00:00Z");
        Instant createdAt = Instant.parse("2026-01-10T00:00:00Z");
        Instant updatedAt = Instant.parse("2026-01-10T00:00:00Z");
        Long id=10L;
        Task task1=new Task(
                id,
                "cook",
                "cook a hot meal",
                Status.ACTIVE,
                dueDate,
                createdAt,
                updatedAt
        );
        Task task2=new Task(
                20L,
                "cook",
                "cook a hot meal",
                Status.ACTIVE,
                dueDate,
                createdAt,
                updatedAt
        );
        when(taskRepository.findAll()).thenReturn(List.of(task1,task2));
        //Act
        List<TaskResponseDTO> tasks=taskService.findAllTasks();
        //Assert
        assertEquals(10L,tasks.get(0).id());
        assertEquals(20L,tasks.get(1).id());

        verify(taskRepository).findAll();
    }
    @Test
    void updateTask_shouldUpdateTaskIfItExitsAndReturnNewTask(){
        //Arrange
        Long id=1L;
        TaskRequestDTO taskRequestDTO=new TaskRequestDTO(
                "run",
                "marathon run",
                Status.ACTIVE,
                Instant.parse("2026-01-10T00:00:00Z")
        );
        Task task=new Task(
                id,
                "ride",
                "ride the bike",
                taskRequestDTO.status(),
                Instant.parse("2026-01-10T00:00:00Z"),
                Instant.parse("2026-01-10T00:00:00Z"),
                Instant.parse("2026-01-10T00:00:00Z")

        );
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn (new Task(
                id,
                taskRequestDTO.title(),
                taskRequestDTO.description(),
                taskRequestDTO.status(),
                Instant.parse("2026-01-10T00:00:00Z"),
                Instant.parse("2026-01-10T00:00:00Z"),
                Instant.parse("2026-01-10T00:00:00Z")
        ));

        //Act
        TaskResponseDTO updatedTask= taskService.updateTask(id,taskRequestDTO);
        //Assert
        assertEquals("run",updatedTask.title());
        verify(taskRepository).save(task);
        verify(taskRepository).findById(id);
    }

    @Test
    void deleteTask_whenTaskExists(){
        //Arrange
        Instant dueDate = Instant.parse("2026-01-10T00:00:00Z");
        Instant createdAt = Instant.parse("2026-01-10T00:00:00Z");
        Instant updatedAt = Instant.parse("2026-01-10T00:00:00Z");
        Task task =new Task(
                1L,
                "eat",
                "eat lasgna",
                Status.ACTIVE,
                dueDate,
                createdAt,
                updatedAt
        );


        Long id=1L;
        when(taskRepository.findById(id)).thenReturn(Optional.of(
                task
        ));

        //Act
        taskService.deleteTaskById(id);
        //Assert

        verify(taskRepository).deleteById(id);
        verify(taskRepository).findById(id);
    }


}*/