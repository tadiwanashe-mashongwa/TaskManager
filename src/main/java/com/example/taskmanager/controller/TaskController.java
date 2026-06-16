package com.example.taskmanager.controller;

import com.example.taskmanager.dto.ApiResponse;
import com.example.taskmanager.dto.TaskRequestDTO;
import com.example.taskmanager.dto.TaskResponseDTO;
import com.example.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponseDTO>> newTask(@RequestBody TaskRequestDTO taskRequestDTO) {
        TaskResponseDTO taskResponse = taskService.createTask(taskRequestDTO);

        // 💡 Refactored: Cleaner status initialization block
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Task created successfully", taskResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> getTaskById(@PathVariable Long id) {
        TaskResponseDTO taskResponseDTO = taskService.findTaskById(id);

        // 💡 Refactored: Replaced status(HttpStatus.OK).body() with the clean .ok() factory shortcut
        return ResponseEntity.ok(ApiResponse.success("Task retrieved successfully", taskResponseDTO));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskResponseDTO>>> getAllTasks() {
        List<TaskResponseDTO> taskResponseDTOS = taskService.findAllTasks();

        // 💡 Refactored: Fixed the public "restrived" spelling typo in the string response
        return ResponseEntity.ok(ApiResponse.success("All tasks retrieved successfully", taskResponseDTOS));
    }

    @DeleteMapping("/{id}")
    // 💡 FIXED: Returning 200 OK because we are explicitly delivering a JSON confirmation payload envelope
    public ResponseEntity<ApiResponse<Void>> deleteTaskById(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.ok(ApiResponse.success("Task deleted successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponseDTO>> updateTask(@PathVariable Long id, @RequestBody TaskRequestDTO taskRequestDTO) {
        TaskResponseDTO updatedTask = taskService.updateTask(id, taskRequestDTO);
        return ResponseEntity.ok(ApiResponse.success("Task updated successfully", updatedTask));
    }
}