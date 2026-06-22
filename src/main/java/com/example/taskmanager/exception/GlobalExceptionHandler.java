package com.example.taskmanager.exception;

import com.example.taskmanager.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(ResourceNotFoundException ex, WebRequest webRequest) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                "Resource Not Found",
                ex.getMessage(),
                webRequest.getDescription(false).replace("uri","")


        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDTO);
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorResponseDTO> handleConflict(ResourceConflictException ex,WebRequest webRequest) {
        ErrorResponseDTO errorResponseDTO=new ErrorResponseDTO(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                "conflict",
                ex.getMessage(),
                webRequest.getDescription(false).replace("uri","")
        );

        return  ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponseDTO); // Returns HTTP 409
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();

        // Loop through every validation failure inside the framework binding target
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponseDTO errorBody = new ErrorResponseDTO(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Validation failed for " + errors.size() + " field(s)",
                request.getDescription(false).replace("uri=", ""),
                errors // Pass the accumulated failures directly to the client payload
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
    }
}