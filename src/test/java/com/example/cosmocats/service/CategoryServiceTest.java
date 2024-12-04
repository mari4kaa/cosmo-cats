package com.example.cosmocats.service;

import com.example.cosmocats.dto.CategoryDto;
import com.example.cosmocats.entities.CategoryEntity;
import com.example.cosmocats.mapper.CategoryMapper;
import com.example.cosmocats.repository.CategoryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    private CategoryService categoryService;
    private CategoryRepository categoryRepository;

    private CategoryDto cosmicCategoryDto;
    private CategoryMapper categoryMapper;

    private UUID categoryUUID;

    @BeforeEach
    void setUp() {
        categoryMapper = Mappers.getMapper(CategoryMapper.class);
        categoryRepository = mock(CategoryRepository.class);
        categoryService = new CategoryService(categoryRepository);

        categoryUUID = UUID.randomUUID();

        cosmicCategoryDto = CategoryDto.builder()
                .id(categoryUUID)
                .name("Nebula Resources")
                .build();
    }

    @Test
    void getCategoryById_withValidId_shouldReturnCategory() {
        CategoryEntity categoryEntity = categoryMapper.dtoToEntity(cosmicCategoryDto);

        when(categoryRepository.findById(categoryUUID.getMostSignificantBits())).thenReturn(Optional.of(categoryEntity));

        Optional<CategoryDto> result = categoryService.getCategoryById(categoryUUID.getMostSignificantBits());

        assertNotNull(result);
        assertEquals("Nebula Resources", result.get().getName());
    }

    @Test
    void getCategoryById_withInvalidId_shouldReturnNull() {
        when(categoryRepository.findById(categoryUUID.getMostSignificantBits())).thenReturn(Optional.empty());

        Optional<CategoryDto> result = categoryService.getCategoryById(categoryUUID.getMostSignificantBits());

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllCategories_shouldReturnCategoryList() {
        CategoryEntity categoryEntity = categoryMapper.dtoToEntity(cosmicCategoryDto);

        when(categoryRepository.findAll()).thenReturn(List.of(categoryEntity));

        List<CategoryDto> categories = categoryService.getAllCategories();

        assertNotNull(categories);
        assertEquals(1, categories.size());
        assertEquals("Nebula Resources", categories.get(0).getName());
    }

    @Test
    void createCategory_withValidData_shouldReturnCreatedCategory() {
        CategoryEntity categoryEntity = categoryMapper.dtoToEntity(cosmicCategoryDto);

        when(categoryRepository.save(Mockito.any(CategoryEntity.class))).thenReturn(categoryEntity);

        CategoryDto result = categoryService.createCategory(cosmicCategoryDto);

        assertNotNull(result);
        assertEquals("Nebula Resources", result.getName());
    }

    @Test
    void updateCategory_withValidId_shouldUpdateAndReturnCategory() {
        CategoryDto updatedCategoryDto = CategoryDto.builder()
            .id(cosmicCategoryDto.getId())
            .name("Galactic Essentials")
            .build();

        CategoryEntity updatedEntity = categoryMapper.dtoToEntity(updatedCategoryDto);

        when(categoryRepository.findById(categoryUUID.getMostSignificantBits())).thenReturn(Optional.of(updatedEntity));
        when(categoryRepository.save(Mockito.any(CategoryEntity.class))).thenReturn(updatedEntity);

        CategoryDto updatedCategory = categoryService.updateCategory(categoryUUID.getMostSignificantBits(), updatedCategoryDto);

        assertNotNull(updatedCategory);
        assertEquals("Galactic Essentials", updatedCategory.getName());
    }

    @Test
    void deleteCategory_withValidId_shouldRemoveCategory() {
        when(categoryRepository.existsById(categoryUUID.getMostSignificantBits())).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(categoryUUID.getMostSignificantBits());

        categoryService.deleteCategory(categoryUUID.getMostSignificantBits());

        verify(categoryRepository, times(1)).deleteById(categoryUUID.getMostSignificantBits());
    }
}
