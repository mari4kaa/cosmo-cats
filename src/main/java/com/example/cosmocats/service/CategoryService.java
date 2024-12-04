package com.example.cosmocats.service;

import com.example.cosmocats.dto.CategoryDto;
import com.example.cosmocats.dto.ProductDto;
import com.example.cosmocats.entities.CategoryEntity;
import com.example.cosmocats.service.exception.CategoryCreationException;
import com.example.cosmocats.service.exception.CategoryDeletionException;
import com.example.cosmocats.service.exception.CategoryNotFoundException;
import com.example.cosmocats.service.exception.CategoryUpdateException;
import com.example.cosmocats.service.exception.ProductCreationException;
import com.example.cosmocats.mapper.CategoryMapper;
import com.example.cosmocats.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = CategoryMapper.getInstance();
    }

    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        log.info("Checking if category with name '{}' already exists", categoryDto.getName());
        if (categoryRepository.findByName(categoryDto.getName()).isPresent()) {
            log.error("Category with name '{}' already exists.", categoryDto.getName());
            throw new CategoryCreationException(String.format("Category with name '%s' already exists.", categoryDto.getName()));
        }

        CategoryEntity categoryEntity = categoryMapper.dtoToEntity(categoryDto);
        log.info("Creating category with name '{}'", categoryDto.getName());
        categoryEntity = categoryRepository.save(categoryEntity);

        log.info("Category created successfully with ID '{}'", categoryEntity.getId());
        return categoryMapper.entityToDto(categoryEntity);
    }

    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
            .map(categoryMapper::entityToDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<CategoryDto> getCategoryById(Long id) {
        return categoryRepository.findById(id)
            .map(categoryMapper::entityToDto);
    }

    @Transactional
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        log.info("Attempting to update category with ID '{}'", categoryId);
        Optional<CategoryEntity> existingCategory = categoryRepository.findById(categoryId);

        if (existingCategory.isEmpty()) {
            log.error("Category with ID '{}' not found.", categoryId);
            throw new CategoryNotFoundException(String.format("Category with ID '%d' not found.", categoryId));
        }

        CategoryEntity categoryEntity = existingCategory.get();
        categoryEntity.setName(categoryDto.getName());

        try {
            categoryEntity = categoryRepository.save(categoryEntity);
            log.info("Category with ID '{}' updated successfully", categoryId);
        } catch (Exception e) {
            log.error("Failed to update category with ID '{}': {}", categoryId, e.getMessage());
            throw new CategoryUpdateException(String.format("Failed to update category with ID '%d'.", categoryId));
        }

        return categoryMapper.entityToDto(categoryEntity);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {

        if (!categoryRepository.existsById(categoryId)) {
            return;
        }

        try {
            categoryRepository.deleteById(categoryId);
            log.info("Category with ID '{}' deleted successfully", categoryId);
        } catch (Exception e) {
            log.error("Failed to delete category with ID '{}': {}", categoryId, e.getMessage());
            throw new CategoryDeletionException(String.format("Failed to delete category with ID '%d'.", categoryId));
        }
    }
}
