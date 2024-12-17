package com.example.cosmocats.web;

import com.example.cosmocats.dto.CategoryDto;
import com.example.cosmocats.web.exception.CategoryNotFoundException;
import com.example.cosmocats.service.CategoryService;
import com.example.cosmocats.service.exception.category.CategoryCreationException;
import com.example.cosmocats.service.exception.category.CategoryDeletionException;
import com.example.cosmocats.service.exception.category.CategoryUpdateException;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/admin/categories")
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PreAuthorize("hasRole('IMPORTANT_CAT')")
    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("Creating category with name: '{}'", categoryDto.getName());
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
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        log.info("Fetching all categories");
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable UUID id) {
        log.info("Fetching categories with ID '{}'", id);
        return categoryService.getCategoryById(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new CategoryNotFoundException(id.toString()));
    }

    @PreAuthorize("hasRole('IMPORTANT_CAT')")
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable UUID categoryId, @RequestBody @Valid CategoryDto categoryDto) {
        log.info("Updating category with ID: '{}'", categoryId);
        try {
            CategoryDto updatedCategory = categoryService.updateCategory(categoryId, categoryDto);
            log.info("Category updated successfully with ID: '{}'", categoryId);
            return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
        } catch (CategoryUpdateException | CategoryNotFoundException e) {
            log.error("Failed to update category with ID '{}': {}", categoryId, e.getMessage());
            throw e;
        }
    }

    @PreAuthorize("hasRole('IMPORTANT_CAT')")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID categoryId) {
        log.info("Deleting category with ID: '{}'", categoryId);
        try {
            categoryService.deleteCategory(categoryId);
            log.info("Category deleted successfully with ID: '{}'", categoryId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (CategoryDeletionException | CategoryNotFoundException e) {
            log.error("Failed to delete category with ID '{}': {}", categoryId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
