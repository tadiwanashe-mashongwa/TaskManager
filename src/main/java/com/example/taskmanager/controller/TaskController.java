package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskRequestDTO;
import com.example.taskmanager.dto.TaskResponseDTO;
import com.example.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponseDTO> newTask(@RequestBody TaskRequestDTO taskRequestDTO){
        TaskResponseDTO taskResponse= taskService.createTask(taskRequestDTO);
        return  ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id){
        TaskResponseDTO taskResponseDTO=taskService.findTaskById(id);
        return ResponseEntity.status(HttpStatus.OK).body(taskResponseDTO);
    }
}
