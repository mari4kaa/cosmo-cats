package com.example.cosmocats.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;
import java.net.URI;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleProductNotFound(
            ProductNotFoundException ex, WebRequest request) {
        
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        error.setTitle("Not Found");
        error.setDetail(ex.getMessage());
        error.setInstance(URI.create(request.getDescription(false)));
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce("", (a, b) -> a.isEmpty() ? b : a + ", " + b);

        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        error.setTitle("Bad Request");
        error.setDetail("Validation failed: " + message);
        error.setInstance(URI.create(request.getDescription(false)));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        error.setTitle("Bad Request");
        error.setDetail("Validation failed: " + ex.getMessage());
        error.setInstance(URI.create(request.getDescription(false)));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        
        // Create ProblemDetail for invalid UUID format or type mismatch
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        error.setTitle("Invalid Parameter");
        error.setDetail("Invalid UUID format for parameter '" + ex.getName() + "': " + ex.getValue());
        error.setInstance(URI.create(request.getDescription(false)));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
