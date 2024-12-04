package com.example.cosmocats.web;

import com.example.cosmocats.dto.CategoryDto;
import com.example.cosmocats.dto.ProductDto;
import com.example.cosmocats.service.exception.CategoryCreationException;
import com.example.cosmocats.service.exception.CategoryDeletionException;
import com.example.cosmocats.service.exception.CategoryNotFoundException;
import com.example.cosmocats.service.exception.CategoryUpdateException;
import com.example.cosmocats.web.exception.ProductNotFoundException;
import com.example.cosmocats.service.CategoryService;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/categories")
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("Attempting to create category with name: '{}'", categoryDto.getName());
        try {
            CategoryDto createdCategory = categoryService.createCategory(categoryDto);
            log.info("Category created successfully with ID: '{}'", createdCategory.getId());
            return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
        } catch (CategoryCreationException e) {
            log.error("Category creation failed for name '{}': {}", categoryDto.getName(), e.getMessage());
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAllProducts() {
        try {
            log.info("Fetching all categories");
            List<CategoryDto> categoryDtos = categoryService.getAllCategories();
            
            log.info("Retrieved {} categories", categoryDtos.size());
            return ResponseEntity.ok(categoryDtos);
        } catch (Exception e) {
            log.error("Error retrieving categories: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable UUID categoryId) {
        log.info("Attempting to retrieve category with ID: '{}'", categoryId);
        try {
            CategoryDto categoryDto = categoryService.getCategoryById(categoryId.getMostSignificantBits())
                                        .orElseThrow(() -> new CategoryNotFoundException(categoryId.toString()));

            log.info("Category retrieved successfully with ID: '{}'", categoryId);
            return new ResponseEntity<>(categoryDto, HttpStatus.OK);
        } catch (CategoryNotFoundException e) {
            log.error("Category with ID '{}' not found: {}", categoryId, e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable UUID categoryId, @RequestBody @Valid CategoryDto categoryDto) {
        log.info("Attempting to update category with ID: '{}'", categoryId);
        try {
            CategoryDto updatedCategory = categoryService.updateCategory(categoryId.getMostSignificantBits(), categoryDto);
            log.info("Category updated successfully with ID: '{}'", categoryId);
            return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
        } catch (CategoryUpdateException | CategoryNotFoundException e) {
            log.error("Failed to update category with ID '{}': {}", categoryId, e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID categoryId) {
        log.info("Attempting to delete category with ID: '{}'", categoryId);
        try {
            categoryService.deleteCategory(categoryId.getMostSignificantBits());
            log.info("Category deleted successfully with ID: '{}'", categoryId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (CategoryDeletionException | CategoryNotFoundException e) {
            log.error("Failed to delete category with ID '{}': {}", categoryId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
