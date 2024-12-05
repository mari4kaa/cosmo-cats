package com.example.cosmocats.controller;

import com.example.cosmocats.dto.CategoryDto;
import com.example.cosmocats.web.CategoryController;
import com.example.cosmocats.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoryDto cosmicCategoryDto;

    private UUID categoryUUID;

    @BeforeEach
    void setUp() {
        categoryUUID = UUID.randomUUID();

        cosmicCategoryDto = CategoryDto.builder()
                .id(categoryUUID)
                .name("Interstellar Supplies")
                .build();
    }

    @Test
    @SneakyThrows
    void getCategoryById_withValidId_shouldReturnCategory() {
        Mockito.when(categoryService.getCategoryById(categoryUUID))
                .thenReturn(Optional.of(cosmicCategoryDto));

        mockMvc.perform(get("/api/v1/categories/{id}", categoryUUID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Interstellar Supplies"));
    }

    @Test
    @SneakyThrows
    void getCategoryById_withNonExistentId_shouldReturn404() {
        UUID nonExistentUUID = UUID.randomUUID();
        Mockito.when(categoryService.getCategoryById(nonExistentUUID)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/categories/{id}", nonExistentUUID))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getAllCategories_shouldReturnCategoryList() {
        Mockito.when(categoryService.getAllCategories()).thenReturn(List.of(cosmicCategoryDto));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Interstellar Supplies"));
    }

    @Test
    @SneakyThrows
    void createCategory_withValidData_shouldReturnCreatedCategory() {
        Mockito.when(categoryService.createCategory(Mockito.any(CategoryDto.class)))
                .thenReturn(cosmicCategoryDto);

        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cosmicCategoryDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Interstellar Supplies"));
    }

    @Test
    @SneakyThrows
    void updateCategory_withValidData_shouldReturnUpdatedCategory() {
        CategoryDto updatedCategoryDto = CategoryDto.builder()
            .id(cosmicCategoryDto.getId())
            .name("Galactic Essentials")
            .build();

        Mockito.when(categoryService.updateCategory(Mockito.eq(categoryUUID), Mockito.any(CategoryDto.class)))
                .thenReturn(updatedCategoryDto);

        mockMvc.perform(put("/api/v1/categories/{id}", categoryUUID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCategoryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Galactic Essentials"));
    }

    @Test
    @SneakyThrows
    void deleteCategory_withValidId_shouldReturn204() {
        Mockito.doNothing().when(categoryService).deleteCategory(categoryUUID);

        mockMvc.perform(delete("/api/v1/categories/{id}", categoryUUID))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void deleteProduct_withNonExistentId_shouldReturn204() {
        UUID randUUID = UUID.randomUUID();
        Mockito.doNothing().when(categoryService).deleteCategory(randUUID);

        mockMvc.perform(delete(String.format("/api/v1/categories/%s", randUUID)))
                .andExpect(status().isNoContent());
    }
}
