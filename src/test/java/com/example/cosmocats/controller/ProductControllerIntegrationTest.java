package com.example.cosmocats.controller;

import com.example.cosmocats.dto.CategoryDto;
import com.example.cosmocats.dto.ProductDto;
import com.example.cosmocats.entities.CategoryEntity;
import com.example.cosmocats.entities.ProductEntity;
import com.example.cosmocats.mapper.CategoryMapper;
import com.example.cosmocats.mapper.ProductMapper;
import com.example.cosmocats.repository.CategoryRepository;
import com.example.cosmocats.repository.ProductRepository;
import com.example.cosmocats.validation.enums.CosmicOrigins;
import com.example.cosmocats.validation.enums.CosmicWords;
import com.example.cosmocats.web.ProductController;
import com.example.cosmocats.web.exception.GlobalExceptionHandler;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

        @SuppressWarnings("resource")
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
    private ProductRepository productRepository;

    @Autowired
    private ProductController productController;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    private CategoryDto categoryDto;
    private CategoryEntity categoryEntity;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        categoryEntity = CategoryEntity.builder()
            .name("Mock Category")
            .build();

        categoryRepository.save(categoryEntity);
        categoryDto = categoryMapper.entityToDto(categoryEntity);
    }

    @Test
    @SneakyThrows
    void testCreateProductValid() {
        ProductDto validProduct = ProductDto.builder()
                .categoryId(categoryDto.getId())
                .name("Cosmic Widget")
                .description("A widget that is truly cosmic.")
                .origin("Proxima")
                .price(9.99f)
                .build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Cosmic Widget"))
                .andExpect(jsonPath("$.origin").value("Proxima"));
    }

    @Test
    @SneakyThrows
    void testCreateProductInvalidName() {
        ProductDto invalidProduct = ProductDto.builder()
                .categoryId(categoryDto.getId())
                .name("Widget") // missing cosmic word
                .description("An ordinary widget.")
                .origin("Mars")
                .price(9.99f)
                .build();

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.detail")
                        .value(org.hamcrest.Matchers.containsString(
                                "name: Product should contain at least one of cosmic words: "
                                        + String.join(", ", CosmicWords.getValues()))));
    }

    @Test
    @SneakyThrows
    void testCreateProductInvalidOrigin() {
        ProductDto invalidProduct = ProductDto.builder()
                .categoryId(categoryDto.getId())
                .name("Cosmic Widget")
                .description("A widget that is truly cosmic.")
                .origin("Unknown") // not a cosmic origin
                .price(9.99f)
                .build();

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.detail")
                        .value("Validation failed: origin: String should contain at least one of next words: "
                                + String.join(", ", CosmicOrigins.getValues())))
                .andExpect(jsonPath("$.instance").value("uri=/api/v1/products"));
    }

    @Test
    @SneakyThrows
    void testCreateProductNegativePrice() {
        ProductDto invalidProduct = ProductDto.builder()
                .categoryId(categoryDto.getId())
                .name("Intergalactic Widget")
                .description("A widget that is intergalactic.")
                .origin("Earth")
                .price(-10.0f) // negative price
                .build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.detail").value("Validation failed: price: Price cannot be negative"))
                .andExpect(jsonPath("$.instance").value("uri=/api/v1/products"));
    }

    @Test
    @SneakyThrows
    void testCreateProductWithDuplicateName() {
        ProductDto productDto = ProductDto.builder()
            .categoryId(categoryDto.getId())
            .name("Cosmic Widget")
            .description("A widget of cosmic quality.")
            .origin("Mars")
            .price(15.99f)
            .build();

        ProductEntity productEntity = productMapper.dtoToEntity(productDto);
        productRepository.save(productEntity);

        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto)))
        .andExpect(status().isConflict())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.title").value("Conflict"))
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.detail").value("Product with name 'Cosmic Widget' already exists."));
    }
}
