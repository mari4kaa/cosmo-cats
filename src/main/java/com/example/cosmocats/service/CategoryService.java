package com.example.cosmocats.service;

import com.example.cosmocats.dto.CategoryDto;
import com.example.cosmocats.entities.CategoryEntity;
import com.example.cosmocats.web.exception.CategoryNotFoundException;
import com.example.cosmocats.mapper.CategoryMapper;
import com.example.cosmocats.repository.CategoryRepository;
import com.example.cosmocats.service.exception.category.CategoryCreationException;
import com.example.cosmocats.service.exception.category.CategoryDeletionException;
import com.example.cosmocats.service.exception.category.CategoryUpdateException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        if (categoryRepository.findByName(categoryDto.getName()).isPresent()) {
            throw new CategoryCreationException(String.format("Category with name '%s' already exists.", categoryDto.getName()));
        }

        CategoryEntity categoryEntity = categoryMapper.dtoToEntity(categoryDto);
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
    public Optional<CategoryDto> getCategoryById(UUID id) {
        return categoryRepository.findById(categoryMapper.uuidToLong(id))
            .map(categoryMapper::entityToDto);
    }

    @Transactional
    public CategoryDto updateCategory(UUID id, CategoryDto updatedcategoryDto) {
        Long categoryId = categoryMapper.uuidToLong(id);

        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException(id.toString());
        }

        try {
            return categoryRepository.findById(categoryId)
                .map(existingEntity -> {
                    CategoryEntity updatedEntity = categoryMapper.dtoToEntity(updatedcategoryDto);
                    updatedEntity.setId(categoryId);
                    CategoryEntity savedEntity = categoryRepository.save(updatedEntity);
                    log.info("Category updated successfully with ID: {}", categoryId);
                    return categoryMapper.entityToDto(savedEntity);
                })
                .orElseThrow(() -> new CategoryNotFoundException(id.toString()));
        } catch (Exception e) {
            throw new CategoryUpdateException(String.format("Failed to update category: %s", e.getMessage()));
        }
    }

    @Transactional
    public void deleteCategory(UUID id) {
        Long categoryId = categoryMapper.uuidToLong(id);

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
