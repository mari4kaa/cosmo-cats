package com.example.cosmocats.controller;

import com.example.cosmocats.dto.ProductDto;
import com.example.cosmocats.entities.CategoryEntity;
import com.example.cosmocats.entities.ProductEntity;
import com.example.cosmocats.repository.CategoryRepository;
import com.example.cosmocats.repository.ProductRepository;
import com.example.cosmocats.validation.enums.CosmicOrigins;
import com.example.cosmocats.web.ProductController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

@SpringBootTest
@Testcontainers
class ProductControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.2")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

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
    private CategoryRepository categoryRepository;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        productRepository.deleteAll();
    }

    @Test
    void testCreateProductValid() throws Exception {
        ProductDto validProduct = ProductDto.builder()
                .id(UUID.randomUUID())
                .categoryId(UUID.randomUUID())
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
                .id(UUID.randomUUID())
                .categoryId(UUID.randomUUID())
                .name("Widget") // missing cosmic word
                .description("An ordinary widget.")
                .origin("Mars")
                .price(9.99f)
                .build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Product should contain at least one of cosmic words: cosmic, space, intergalactic"));
    }

    @Test
    @SneakyThrows
    void testCreateProductInvalidOrigin() {
        ProductDto invalidProduct = ProductDto.builder()
                .id(UUID.randomUUID())
                .categoryId(UUID.randomUUID())
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
                .id(UUID.randomUUID())
                .categoryId(UUID.randomUUID())
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
        UUID categoryId = UUID.randomUUID();
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .id(categoryId.getMostSignificantBits())
                .name("Cosmic Tools")
                .build();
        categoryRepository.save(categoryEntity);

        ProductDto productDto = ProductDto.builder()
                .id(UUID.randomUUID())
                .categoryId(categoryId)
                .name("Cosmic Widget")
                .description("A widget of cosmic quality.")
                .origin("Mars")
                .price(15.99f)
                .build();

        productRepository.save(ProductEntity.builder()
                .category(categoryEntity)
                .name(productDto.getName())
                .description(productDto.getDescription())
                .origin(productDto.getOrigin())
                .price(productDto.getPrice())
                .build());

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Validation failed: Product with name Cosmic Widget already exists."));
    }
}
