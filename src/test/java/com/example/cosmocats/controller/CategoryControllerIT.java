package com.example.cosmocats.controller;

import com.example.cosmocats.dto.CategoryDto;
import com.example.cosmocats.entities.CategoryEntity;
import com.example.cosmocats.mapper.CategoryMapper;
import com.example.cosmocats.repository.CategoryRepository;
import com.example.cosmocats.web.CategoryController;
import com.example.cosmocats.web.exception.GlobalExceptionHandler;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class CategoryControllerIT {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.2")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @BeforeAll
    static void startContainer() {
        postgres.start();
    }

    @AfterAll
    static void stopContainer() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureTestDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryController categoryController;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        categoryRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "IMPORTANT_CAT")
    @SneakyThrows
    void testCreateCategoryValid() {
        CategoryDto validCategory = CategoryDto.builder()
                .name("Galactic Supplies")
                .build();

        mockMvc.perform(post("/api/v1/internal/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCategory)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Galactic Supplies"));
    }

    @Test
    @WithMockUser(roles = "IMPORTANT_CAT")
    @SneakyThrows
    void testCreateCategoryDuplicateName() {
        CategoryEntity category = CategoryEntity.builder()
                .name("Cosmic Goods")
                .build();
        categoryRepository.save(category);

        CategoryDto duplicateCategory = CategoryDto.builder()
                .name("Cosmic Goods")
                .build();

        mockMvc.perform(post("/api/v1/internal/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateCategory)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Validation failed: Category with name 'Cosmic Goods' already exists."));
    }

    @Test
    @WithMockUser(roles = "IMPORTANT_CAT")
    @SneakyThrows
    void testGetAllCategories() {
        CategoryEntity category1 = categoryRepository.save(CategoryEntity.builder()
                .name("Stellar Devices")
                .build());
        CategoryEntity category2 = categoryRepository.save(CategoryEntity.builder()
                .name("Orbital Tools")
                .build());

        mockMvc.perform(get("/api/v1/internal/categories")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value(category1.getName()))
                .andExpect(jsonPath("$[1].name").value(category2.getName()));
    }

    @Test
    @WithMockUser(roles = "IMPORTANT_CAT")
    @SneakyThrows
    void testGetCategoryById() {
        CategoryEntity category = categoryRepository.save(CategoryEntity.builder()
                .name("Galactic Goods")
                .build());

        CategoryDto categoryDto = categoryMapper.entityToDto(category);

        mockMvc.perform(get("/api/v1/internal/categories/{id}", categoryDto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(category.getName()));
    }

    @Test
    @WithMockUser(roles = "IMPORTANT_CAT")
    @SneakyThrows
    void testGetCategoryByIdNotFound() {
        UUID invalidId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/internal/categories/{id}", invalidId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value(String.format("Category not found with id: %s", invalidId.toString())));
    }

    @Test
    @WithMockUser(roles = "IMPORTANT_CAT")
    @SneakyThrows
    void testUpdateCategory() {
        CategoryEntity existingCategoryEntity = categoryRepository.save(CategoryEntity.builder()
                .name("Old Name")
                .build());

        CategoryDto updatedCategory = CategoryDto.builder()
                .name("Updated Name")
                .build();

        CategoryDto existingCategoryDto = categoryMapper.entityToDto(existingCategoryEntity);

        mockMvc.perform(put("/api/v1/internal/categories/{id}", existingCategoryDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCategory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    @WithMockUser(roles = "IMPORTANT_CAT")
    @SneakyThrows
    void testUpdateCategoryNotFound() {
        UUID invalidId = UUID.randomUUID();

        CategoryDto updatedCategory = CategoryDto.builder()
                .name("New Name")
                .build();

        mockMvc.perform(put("/api/v1/internal/categories/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCategory)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value(String.format("Category not found with id: %s", invalidId.toString())));
    }

    @Test
    @WithMockUser(roles = "IMPORTANT_CAT")
    @SneakyThrows
    void testDeleteCategory() {
        CategoryEntity category = categoryRepository.save(CategoryEntity.builder()
                .name("Deletable Category")
                .build());

        CategoryDto categoryDto = categoryMapper.entityToDto(category);

        mockMvc.perform(delete("/api/v1/internal/categories/{id}", categoryDto.getId()))
                .andExpect(status().isNoContent());

        assertFalse(categoryRepository.findById(category.getId()).isPresent());
    }

    @Test
    @WithMockUser(roles = "IMPORTANT_CAT")
    @SneakyThrows
    void testDeleteCategoryNotFound() {
        UUID invalidId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/internal/categories/{id}", invalidId))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void testAccessWithoutAuthenticationCredentials() {
        mockMvc.perform(post("/api/v1/internal/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CategoryDto.builder().name("No Credentials").build())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Authentication required"))
                .andExpect(jsonPath("$.detail").value("No authentication credentials were found"));
    }


    @Test
    @WithMockUser(roles = "WRONG_ROLE")
    @SneakyThrows
    void testAccessWithIncorrectRole() {
        mockMvc.perform(post("/api/v1/internal/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CategoryDto.builder().name("Unauthorized").build())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("Forbidden"))
                .andExpect(jsonPath("$.detail").value("Access Denied"));
    }
    

    @Test
    @WithMockUser(roles = "IMPORTANT_CAT")
    @SneakyThrows
    void testAccessWithCorrectRole() {
        CategoryDto validCategory = CategoryDto.builder()
                .name("Authorized Access")
                .build();

        mockMvc.perform(post("/api/v1/internal/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCategory)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Authorized Access"));
    }
}
