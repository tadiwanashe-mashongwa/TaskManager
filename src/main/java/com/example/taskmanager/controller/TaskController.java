package com.example.taskmanager.controller;

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
    public ResponseEntity<TaskResponseDTO> newTask(@RequestBody TaskRequestDTO taskRequestDTO){
        TaskResponseDTO taskResponse= taskService.createTask(taskRequestDTO);
        return  ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id){
        TaskResponseDTO taskResponseDTO=taskService.findTaskById(id);
        return ResponseEntity.status(HttpStatus.OK).body(taskResponseDTO);
    }

    @GetMapping
    public List<TaskResponseDTO> getAllTasks(){
        List<TaskResponseDTO> taskResponseDTOS=taskService.findAllTasks();
        return taskResponseDTOS;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long id) {
        taskService.deleteTaskById(id);
    return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long id,@RequestBody TaskRequestDTO taskRequestDTO){

        TaskResponseDTO updatedTask=taskService.updateTask(id,taskRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
    }
}
