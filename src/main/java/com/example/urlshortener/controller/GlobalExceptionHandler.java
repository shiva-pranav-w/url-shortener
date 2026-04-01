package com.example.urlshortener.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex){

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Validation failed");
        response.put("errors", errors);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRunTimeException(RuntimeException ex){
        if(ex.getMessage().equals("Link expired")){
            return ResponseEntity.status(410).body(ex.getMessage());
        }
        if(ex.getMessage().equals("URL not found")){
            return ResponseEntity.status(404).body(ex.getMessage());
        }
        if(ex.getMessage().equals("Too many requests")){
            return ResponseEntity.status(429).body(ex.getMessage());
        }
        if(ex.getMessage().equals("Custom code already in use")){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
        if(ex.getMessage().equals("Email already registered")){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
        if(ex.getMessage().equals("Invalid credentials")){
            return ResponseEntity.status(401).body(ex.getMessage());
        }
        if(ex.getMessage().equals("Unauthorized")){
            return ResponseEntity.status(401).body(ex.getMessage());
        }
        if(ex.getMessage().equals("Forbidden")){
            return ResponseEntity.status(403).body(ex.getMessage());
        }
        return ResponseEntity.status(500).body("Something went wrong");
    }

}
