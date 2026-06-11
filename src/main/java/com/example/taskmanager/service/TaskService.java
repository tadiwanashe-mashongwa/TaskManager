package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskRequestDTO;
import com.example.taskmanager.dto.TaskResponseDTO;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskResponseDTO createTask(TaskRequestDTO taskRequestDTO){
        Task task=new Task();
                task.setTitle(taskRequestDTO.title());
                task.setDescription(taskRequestDTO.description());
                task.setStatus(taskRequestDTO.status());
                task.setDueDate(taskRequestDTO.dueDate());
                Task savedTask=taskRepository.save(task);
                return new TaskResponseDTO(
                        savedTask.getId(),
                        savedTask.getTitle(),
                        savedTask.getDescription(),
                        savedTask.getStatus(),
                        savedTask.getDueDate(),
                        savedTask.getCreatedAt(),
                        savedTask.getUpdatedAt()
                );

    }
}
