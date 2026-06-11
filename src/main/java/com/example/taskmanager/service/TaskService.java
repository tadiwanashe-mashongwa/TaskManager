package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskRequestDTO;
import com.example.taskmanager.dto.TaskResponseDTO;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
    public TaskResponseDTO findTaskById(Long id){
       Task savedTask =taskRepository.findById(id).orElseThrow(()->new RuntimeException("not found"));
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
    public List<TaskResponseDTO> findAllTasks(){
        List<Task> tasks=taskRepository.findAll();
        List<TaskResponseDTO> taskResponseDTOS=new ArrayList<>();
        tasks.forEach(task -> {
           taskResponseDTOS.add(new TaskResponseDTO(
                    task.getId(),
                    task.getTitle(),
                    task.getDescription(),
                    task.getStatus(),
                    task.getDueDate(),
                    task.getCreatedAt(),
                    task.getUpdatedAt()
            ));
        });
        return taskResponseDTOS;
    }
}
