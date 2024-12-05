package com.example.cosmocats.web.exception;

import com.example.cosmocats.featuretoggle.exceptions.FeatureNotAvailableException;
import com.example.cosmocats.service.exception.*;
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

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleCategoryNotFound(
            CategoryNotFoundException ex, WebRequest request) {
        
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        error.setTitle("Not Found");
        error.setDetail(ex.getMessage());
        error.setInstance(URI.create(request.getDescription(false)));
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ProductCreationException.class)
    public ResponseEntity<ProblemDetail> handleProductCreationException(
            ProductCreationException ex, WebRequest request) {
        
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("Conflict");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setInstance(URI.create(request.getDescription(false)));

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    @ExceptionHandler(CategoryCreationException.class)
    public ResponseEntity<ProblemDetail> handleCategoryCreationException(
            CategoryCreationException ex, WebRequest request) {
        
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        error.setTitle("Bad Request");
        error.setDetail(String.format("Validation failed: %s", ex.getMessage()));
        error.setInstance(URI.create(request.getDescription(false)));

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(ProductUpdateException.class)
    public ResponseEntity<ProblemDetail> handleProductUpdateException(
            ProductUpdateException ex, WebRequest request) {
        
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        error.setTitle("Bad Request");
        error.setDetail(String.format("Update failed: %s", ex.getMessage()));
        error.setInstance(URI.create(request.getDescription(false)));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(CategoryUpdateException.class)
    public ResponseEntity<ProblemDetail> handleCategoryUpdateException(
            CategoryUpdateException ex, WebRequest request) {
        
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        error.setTitle("Bad Request");
        error.setDetail(String.format("Update failed: %s", ex.getMessage()));
        error.setInstance(URI.create(request.getDescription(false)));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ProductDeletionException.class)
    public ResponseEntity<ProblemDetail> handleProductDeletionException(
            ProductDeletionException ex, WebRequest request) {
        
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        error.setTitle("Bad Request");
        error.setDetail(String.format("Deletion failed: %s", ex.getMessage()));
        error.setInstance(URI.create(request.getDescription(false)));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(CategoryDeletionException.class)
    public ResponseEntity<ProblemDetail> handleCategoryDeletionException(
            CategoryDeletionException ex, WebRequest request) {
        
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        error.setTitle("Bad Request");
        error.setDetail(String.format("Deletion failed: %s", ex.getMessage()));
        error.setInstance(URI.create(request.getDescription(false)));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleOrderNotFound(
            OrderNotFoundException ex, WebRequest request) {
        
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        error.setTitle("Not Found");
        error.setDetail(ex.getMessage());
        error.setInstance(URI.create(request.getDescription(false)));
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(OrderCreationException.class)
    public ResponseEntity<ProblemDetail> handleOrderCreationException(
            OrderCreationException ex, WebRequest request) {
        
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        error.setTitle("Bad Request");
        error.setDetail(String.format("Validation failed: %s", ex.getMessage()));
        error.setInstance(URI.create(request.getDescription(false)));

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(OrderUpdateException.class)
    public ResponseEntity<ProblemDetail> handleOrderUpdateException(
            OrderUpdateException ex, WebRequest request) {
        
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        error.setTitle("Bad Request");
        error.setDetail(String.format("Update failed: %s", ex.getMessage()));
        error.setInstance(URI.create(request.getDescription(false)));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(OrderDeletionException.class)
    public ResponseEntity<ProblemDetail> handleOrderDeletionException(
            OrderDeletionException ex, WebRequest request) {
        
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        error.setTitle("Bad Request");
        error.setDetail(String.format("Deletion failed: %s", ex.getMessage()));
        error.setInstance(URI.create(request.getDescription(false)));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .reduce("", (a, b) -> a.isEmpty() ? b : String.format("%s, %s", a, b));

        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        error.setTitle("Bad Request");
        error.setDetail(String.format("Validation failed: %s", message));
        error.setInstance(URI.create(request.getDescription(false)));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {
        
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        error.setTitle("Bad Request");
        error.setDetail(String.format("Validation failed: %s", ex.getMessage()));
        error.setInstance(URI.create(request.getDescription(false)));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        
        // Create ProblemDetail for invalid UUID format or type mismatch
        ProblemDetail error = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        error.setTitle("Invalid Parameter");
        error.setDetail(String.format("Invalid UUID format for parameter '%s': %s", ex.getName(), ex.getValue()));
        error.setInstance(URI.create(request.getDescription(false)));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(FeatureNotAvailableException.class)
    public ResponseEntity<String> handleFeatureNotAvailable(FeatureNotAvailableException ex) {
        return ResponseEntity.notFound().build();
    }
}
