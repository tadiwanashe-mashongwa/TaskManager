package com.example.taskmanager.controller;

import com.example.taskmanager.dto.AuthRequestDTO;
import com.example.taskmanager.dto.AuthResponseDTO;
import com.example.taskmanager.dto.LoginRequestDTO;
import com.example.taskmanager.dto.LoginResponseDTO;
import com.example.taskmanager.service.AuthService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody AuthRequestDTO authRequestDTO) {
         return  ResponseEntity.status(HttpStatus.CREATED).body(authService.createUser(authRequestDTO));


    }

    @GetMapping
    public ResponseEntity<List<AuthResponseDTO>> getAllTasks(){
        List<AuthResponseDTO> users =authService.findAllUsers();
        return  ResponseEntity.status(HttpStatus.OK).body(users);
    }
    @PutMapping("/{id}")
    public ResponseEntity<AuthResponseDTO> updateTask(@PathVariable("id") Long id,@Valid @RequestBody AuthRequestDTO authRequestDTO){
        return ResponseEntity.status(HttpStatus.OK).body(authService.updateUser(id,authRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") Long id){
        authService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<AuthResponseDTO> getUserById(@PathVariable("id") Long id){
        return ResponseEntity.status(HttpStatus.OK).body(authService.findUserById(id));
    }

    @PostMapping("/login")
    public  ResponseEntity<LoginResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO){
        LoginResponseDTO loginResponseDTO=authService.loginUser(loginRequestDTO.email(),loginRequestDTO.password());
        return ResponseEntity.status(HttpStatus.OK).body(loginResponseDTO);
    }


}
