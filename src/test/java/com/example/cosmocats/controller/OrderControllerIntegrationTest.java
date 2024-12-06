package com.example.cosmocats.controller;

import com.example.cosmocats.dto.CategoryDto;
import com.example.cosmocats.dto.ProductDto;
import com.example.cosmocats.dto.order.OrderDto;
import com.example.cosmocats.dto.order.OrderEntryDto;
import com.example.cosmocats.entities.CategoryEntity;
import com.example.cosmocats.entities.OrderEntity;
import com.example.cosmocats.entities.OrderEntryEntity;
import com.example.cosmocats.entities.ProductEntity;
import com.example.cosmocats.mapper.CategoryMapper;
import com.example.cosmocats.mapper.OrderMapper;
import com.example.cosmocats.mapper.ProductMapper;
import com.example.cosmocats.repository.CategoryRepository;
import com.example.cosmocats.repository.OrderEntryRepository;
import com.example.cosmocats.repository.OrderRepository;
import com.example.cosmocats.repository.ProductRepository;
import com.example.cosmocats.web.OrderController;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

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
    private OrderRepository orderRepository;

    @Autowired
    private OrderEntryRepository orderEntryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderController orderController;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    private MockMvc mockMvc;

    private ProductEntity product1;
    private ProductEntity product2;
    private ProductEntity product3;

    private ProductDto product1Dto;
    private ProductDto product2Dto;
    private ProductDto product3Dto;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        orderRepository.deleteAll();
        orderEntryRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        CategoryEntity categoryEntity = categoryRepository.save(CategoryEntity.builder()
            .name("Mock Category")
            .build());

        CategoryDto categoryDto = categoryMapper.entityToDto(categoryEntity);

        product1 = productRepository.save(ProductEntity.builder()
                .category(categoryEntity)
                .name("Cosmic Widget")
                .description("A widget of cosmic quality.")
                .price(19.99f)
                .build());

        product2 = productRepository.save(ProductEntity.builder()
                .category(categoryEntity)
                .name("Cosmic Gadget")
                .description("A gadget for the galaxy.")
                .price(29.99f)
                .build());

        product3 = productRepository.save(ProductEntity.builder()
                .category(categoryEntity)
                .name("Cosmic Tool")
                .description("A tool for the stars.")
                .price(39.99f)
                .build());

        product1Dto = productMapper.entityToDto(product1);
        product2Dto = productMapper.entityToDto(product2);
        product3Dto = productMapper.entityToDto(product3);
    }

    @Test
    @SneakyThrows
    void testCreateOrder() {
        OrderDto orderDto = OrderDto.builder()
                .price(49.98f)
                .entryDtos(Arrays.asList(
                        OrderEntryDto.builder()
                                .productId(productMapper.longToUuid(product1.getId()))
                                .quantity(2)
                                .build(),
                        OrderEntryDto.builder()
                                .productId(productMapper.longToUuid(product2.getId()))
                                .quantity(1)
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price").value(49.98f));
    }
}
    /*@Test
    @SneakyThrows
    void testCreateOrderInvalidPrice() {
        OrderDto orderDto = OrderDto.builder()
                .price(-10f) // Invalid price
                .entries(Arrays.asList(
                        OrderEntryDto.builder()
                                .productId(product1Dto.getId())
                                .quantity(2)
                                .build(),
                        OrderEntryDto.builder()
                                .productId(product1Dto.getId())
                                .quantity(1)
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation failed: price: Price cannot be negative"));
    }

    @Test
    @SneakyThrows
    void testGetOrder() {

        OrderEntity existingOrder = orderRepository.save(OrderEntity.builder()
                .price(69.98f)
                .entries(Arrays.asList())
                .build());

        OrderEntryEntity orderEntry1 = orderEntryRepository.save(OrderEntryEntity.builder()
            .order(existingOrder)
            .product(product1)
            .quantity(2)
            .build());

        OrderEntryEntity orderEntry2 = orderEntryRepository.save(OrderEntryEntity.builder()
            .order(existingOrder)
            .product(product1)
            .quantity(2)
            .build());

        existingOrder.setEntries(Arrays.asList(orderEntry1, orderEntry2));

        mockMvc.perform(get("/api/v1/orders/{id}", existingOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(69.98f));
    }

    @Test
    @SneakyThrows
    void testGetOrderNotFound() {
        mockMvc.perform(get("/api/v1/orders/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void testUpdateOrder() {
        OrderEntity existingOrder = orderRepository.save(OrderEntity.builder()
                .price(69.98f)
                .entries(Arrays.asList(
                        OrderEntryEntity.builder()
                                .product(product1)
                                .quantity(2)
                                .build(),
                        OrderEntryEntity.builder()
                                .product(product2)
                                .quantity(1)
                                .build()
                ))
                .build());
        
        OrderDto existingOrderDto = orderMapper.entityToDto(existingOrder);

        OrderDto updatedOrder = OrderDto.builder()
                .entryIds(Collections.singletonList(product3Dto.getId()))
                .price(39.99f)
                .build();

        mockMvc.perform(put("/api/v1/orders/{id}", existingOrderDto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(39.99f));
    }

    @Test
    @SneakyThrows
    void testUpdateOrderNotFound() {
        OrderDto updatedOrder = OrderDto.builder()
                .entryIds(Collections.singletonList(product3Dto.getId()))
                .price(39.99f)
                .build();

        mockMvc.perform(put("/api/v1/orders/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedOrder)))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void testDeleteOrder() {
        OrderEntity existingOrder = orderRepository.save(OrderEntity.builder()
                .price(69.98f)
                .entries(Arrays.asList(
                        OrderEntryEntity.builder()
                                .product(product1)
                                .quantity(2)
                                .build(),
                        OrderEntryEntity.builder()
                                .product(product2)
                                .quantity(1)
                                .build()
                ))
                .build());

        mockMvc.perform(delete("/api/v1/orders/{id}", existingOrder.getId()))
                .andExpect(status().isNoContent());

        assertFalse(orderRepository.existsById(existingOrder.getId()));
    }

    @Test
    @SneakyThrows
    void testDeleteOrderNotFound() {
        mockMvc.perform(delete("/api/v1/orders/{id}", UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void testGetMostFrequentProducts() {
        orderRepository.save(OrderEntity.builder()
                .price(49.98f)
                .entries(Arrays.asList(
                        OrderEntryEntity.builder()
                                .product(product1)
                                .quantity(1)
                                .build(),
                        OrderEntryEntity.builder()
                                .product(product2)
                                .quantity(1)
                                .build()
                ))
                .build());

        orderRepository.save(OrderEntity.builder()
                .price(19.99f)
                .entries(Arrays.asList(
                        OrderEntryEntity.builder()
                                .product(product1)
                                .quantity(1)
                                .build()
                ))
                .build());

        orderRepository.save(OrderEntity.builder()
                .price(59.98f)
                .entries(Arrays.asList(
                        OrderEntryEntity.builder()
                                .product(product1)
                                .quantity(1)
                                .build(),
                        OrderEntryEntity.builder()
                                .product(product3)
                                .quantity(1)
                                .build()
                ))
                .build());

        mockMvc.perform(get("/api/v1/orders/most-frequent-order-entries")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3)) // Expect all three products
                .andExpect(jsonPath("$[0].name").value(product1.getName())) // Product1 should be the most frequent
                .andExpect(jsonPath("$[1].name").value(product2.getName())) // Product2 and Product3 in subsequent places
                .andExpect(jsonPath("$[2].name").value(product3.getName()));
    }

    @Test
    @SneakyThrows
    void testGetMostFrequentProductsNoOrders() {
        mockMvc.perform(get("/api/v1/orders/most-frequent-order-entries")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0)); // No orders, so no products returned
    }

    @Test
    @SneakyThrows
    void testGetMostFrequentProductsTie() {
        // Create orders with tied frequency
        orderRepository.save(OrderEntity.builder()
                .price(49.98f)
                .entries(Arrays.asList(
                        OrderEntryEntity.builder()
                                .product(product1)
                                .quantity(1)
                                .build(),
                        OrderEntryEntity.builder()
                                .product(product2)
                                .quantity(1)
                                .build()
                ))
                .build());

        orderRepository.save(OrderEntity.builder()
                .price(49.98f)
                .entries(Arrays.asList(
                        OrderEntryEntity.builder()
                                .product(product1)
                                .quantity(1)
                                .build(),
                        OrderEntryEntity.builder()
                                .product(product2)
                                .quantity(1)
                                .build()
                ))
                .build());

        mockMvc.perform(get("/api/v1/orders/most-frequent-order-entries")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)) // Expect two products
                .andExpect(jsonPath("$[0].name").value(product1.getName())) // Either product1 or product2 could be first
                .andExpect(jsonPath("$[1].name").value(product2.getName()));
    }*/
