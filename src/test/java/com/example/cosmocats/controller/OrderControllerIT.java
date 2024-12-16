package com.example.cosmocats.controller;

import com.example.cosmocats.dto.order.OrderDto;
import com.example.cosmocats.dto.order.OrderEntryDto;
import com.example.cosmocats.entities.CategoryEntity;
import com.example.cosmocats.entities.OrderEntity;
import com.example.cosmocats.entities.ProductEntity;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class OrderControllerIT {

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

    private MockMvc mockMvc;

    private ProductEntity product1;
    private ProductEntity product2;
    private ProductEntity product3;

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
    }

    @Test
    @SneakyThrows
    void testCreateOrder() {
        OrderDto orderDto = OrderDto.builder()
                .bankCardId("4111-1111-1111-1111")
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
                .andExpect(jsonPath("$.bankCardId").value("4111-1111-1111-1111"))
                .andExpect(jsonPath("$.price").value(49.98f));
    }

    @Test
    @SneakyThrows
    void testCreateOrderInvalidPrice() {
        OrderDto orderDto = OrderDto.builder()
                .bankCardId("4111-1111-1111-1111")
                .price(-10f) // Invalid price
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation failed: price: Price cannot be negative"));
    }

    @Test
    @SneakyThrows
    void testGetOrderById() {

        OrderDto existingOrderDto = OrderDto.builder()
                .bankCardId("4111-1111-1111-1111")
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
        
        OrderEntity existingOrderEntity = orderRepository.save(orderMapper.dtoToEntity(existingOrderDto));

        mockMvc.perform(get("/api/v1/orders/{id}", orderMapper.longToUuid(existingOrderEntity.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bankCardId").value("4111-1111-1111-1111"))
                .andExpect(jsonPath("$.price").value(49.98f));
    }

    @Test
    @SneakyThrows
    void testGetOrderNotFound() {
        mockMvc.perform(get("/api/v1/orders/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void testGetAllOrdersEmpty() {
        mockMvc.perform(get("/api/v1/orders")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @SneakyThrows
    void testGetAllOrdersByBankCardId() {

        OrderDto orderDto1 = OrderDto.builder()
                .bankCardId("4111-1111-1111-1111")
                .price(49.98f)
                .entryDtos(Arrays.asList(
                        OrderEntryDto.builder()
                                .productId(productMapper.longToUuid(product1.getId()))
                                .quantity(1)
                                .build(),
                        OrderEntryDto.builder()
                                .productId(productMapper.longToUuid(product2.getId()))
                                .quantity(1)
                                .build()
                ))
                .build();

        OrderDto orderDto2 = OrderDto.builder()
                .bankCardId("4111-1111-1111-1111")
                .price(19.99f)
                .entryDtos(Arrays.asList(
                        OrderEntryDto.builder()
                                .productId(productMapper.longToUuid(product1.getId()))
                                .quantity(1)
                                .build()
                ))
                .build();

        OrderEntity orderEntity1 = orderRepository.save(orderMapper.dtoToEntity(orderDto1));
        OrderEntity orderEntity2 = orderRepository.save(orderMapper.dtoToEntity(orderDto2));

        UUID expectedId1 = orderMapper.longToUuid(orderEntity1.getId());
        UUID expectedId2 = orderMapper.longToUuid(orderEntity2.getId());

        mockMvc.perform(get("/api/v1/orders/by-card/{bankCardId}", orderEntity1.getBankCardId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id", anyOf(is(expectedId1.toString()), is(expectedId2.toString()))))
                .andExpect(jsonPath("$[1].id", anyOf(is(expectedId1.toString()), is(expectedId2.toString()))));
    }

    @Test
    @SneakyThrows
    void testGetAllOrders() {

        OrderDto orderDto1 = OrderDto.builder()
                .bankCardId("4111-1111-1111-1111")
                .price(49.98f)
                .entryDtos(Arrays.asList(
                        OrderEntryDto.builder()
                                .productId(productMapper.longToUuid(product1.getId()))
                                .quantity(1)
                                .build(),
                        OrderEntryDto.builder()
                                .productId(productMapper.longToUuid(product2.getId()))
                                .quantity(1)
                                .build()
                ))
                .build();

        OrderDto orderDto2 = OrderDto.builder()
                .bankCardId("5431-1111-1111-1111")
                .price(19.99f)
                .entryDtos(Arrays.asList(
                        OrderEntryDto.builder()
                                .productId(productMapper.longToUuid(product1.getId()))
                                .quantity(1)
                                .build()
                ))
                .build();

        OrderEntity orderEntity1 = orderRepository.save(orderMapper.dtoToEntity(orderDto1));
        OrderEntity orderEntity2 = orderRepository.save(orderMapper.dtoToEntity(orderDto2));

        UUID expectedId1 = orderMapper.longToUuid(orderEntity1.getId());
        UUID expectedId2 = orderMapper.longToUuid(orderEntity2.getId());

        mockMvc.perform(get("/api/v1/orders")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id", anyOf(is(expectedId1.toString()), is(expectedId2.toString()))))
                .andExpect(jsonPath("$[1].id", anyOf(is(expectedId1.toString()), is(expectedId2.toString()))));
    }

    @Test
    @SneakyThrows
    void testUpdateOrder() {
        OrderDto existingOrderDto = OrderDto.builder()
                .bankCardId("5431-1111-1111-1111")
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
        
        OrderEntity existingOrderEntity = orderRepository.save(orderMapper.dtoToEntity(existingOrderDto));

        OrderDto updatedOrderDto = OrderDto.builder()
                .bankCardId("5431-1111-1111-1111")
                .price(29.99f)
                .entryDtos(Arrays.asList(
                        OrderEntryDto.builder()
                                .productId(productMapper.longToUuid(product1.getId()))
                                .quantity(1)
                                .build()
                ))
                .build();

        mockMvc.perform(put("/api/v1/orders/{id}", orderMapper.longToUuid(existingOrderEntity.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedOrderDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(29.99f));
    }

    @Test
    @SneakyThrows
    void testUpdateOrderChangeCard() {
        OrderDto existingOrderDto = OrderDto.builder()
                .bankCardId("5431-1111-1111-1111")
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
        
        OrderEntity existingOrderEntity = orderRepository.save(orderMapper.dtoToEntity(existingOrderDto));

        OrderDto updatedOrderDto = OrderDto.builder()
                .bankCardId("5431-5555-5555-5555") // Invalid: changed card number
                .price(29.99f)
                .entryDtos(Arrays.asList(
                        OrderEntryDto.builder()
                                .productId(productMapper.longToUuid(product1.getId()))
                                .quantity(1)
                                .build()
                ))
                .build();

        mockMvc.perform(put("/api/v1/orders/{id}", orderMapper.longToUuid(existingOrderEntity.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedOrderDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Update failed: The bank card ID cannot be changed in the order"));
    }

    @Test
    @SneakyThrows
    void testUpdateOrderNotFound() {
        OrderDto updatedOrderDto = OrderDto.builder()
                .bankCardId("5431-1111-1111-1111")
                .price(29.99f)
                .entryDtos(Arrays.asList(
                        OrderEntryDto.builder()
                                .productId(productMapper.longToUuid(product1.getId()))
                                .quantity(2)
                                .build()
                ))
                .build();

        mockMvc.perform(put("/api/v1/orders/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedOrderDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void testDeleteOrder() {
        OrderDto orderDto = OrderDto.builder()
                .bankCardId("5431-1111-1111-1111")
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
        
        OrderEntity orderEntity = orderRepository.save(orderMapper.dtoToEntity(orderDto));

        mockMvc.perform(delete("/api/v1/orders/{id}", orderMapper.longToUuid(orderEntity.getId())))
                .andExpect(status().isNoContent());

        assertFalse(orderRepository.existsById(orderEntity.getId()));
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

        OrderDto orderDto1 = OrderDto.builder()
                .bankCardId("5431-1111-1111-1111")
                .price(49.98f)
                .entryDtos(Arrays.asList(
                        OrderEntryDto.builder()
                                .productId(productMapper.longToUuid(product1.getId()))
                                .quantity(1)
                                .build(),
                        OrderEntryDto.builder()
                                .productId(productMapper.longToUuid(product2.getId()))
                                .quantity(1)
                                .build()
                ))
                .build();

        OrderDto orderDto2 = OrderDto.builder()
                .bankCardId("5431-1111-1111-1112")
                .price(19.99f)
                .entryDtos(Arrays.asList(
                        OrderEntryDto.builder()
                                .productId(productMapper.longToUuid(product1.getId()))
                                .quantity(1)
                                .build()
                ))
                .build();

        OrderDto orderDto3 = OrderDto.builder()
                .bankCardId("5431-1111-1111-1113")
                .price(59.98f)
                .entryDtos(Arrays.asList(
                        OrderEntryDto.builder()
                                .productId(productMapper.longToUuid(product1.getId()))
                                .quantity(1)
                                .build(),
                        OrderEntryDto.builder()
                                .productId(productMapper.longToUuid(product3.getId()))
                                .quantity(1)
                                .build()
                ))
                .build();

        orderRepository.save(orderMapper.dtoToEntity(orderDto1));
        orderRepository.save(orderMapper.dtoToEntity(orderDto2));
        orderRepository.save(orderMapper.dtoToEntity(orderDto3));

        mockMvc.perform(get("/api/v1/orders/most-frequent-order-entries")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3)) // Expect all three products
                .andExpect(jsonPath("$[0].productName").value(product1.getName())) // Product1 should be the most frequent
                .andExpect(jsonPath("$[1].productName", anyOf(is(product2.getName()), is(product3.getName())))) // Product2 and Product3 in subsequent places
                .andExpect(jsonPath("$[2].productName", anyOf(is(product2.getName()), is(product3.getName()))));
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
        // orders with tied frequency
        OrderDto orderDto1 = OrderDto.builder()
                .bankCardId("5431-1111-1111-1111")
                .price(49.98f)
                .entryDtos(Arrays.asList(
                        OrderEntryDto.builder()
                                .productId(productMapper.longToUuid(product1.getId()))
                                .quantity(1)
                                .build(),
                        OrderEntryDto.builder()
                                .productId(productMapper.longToUuid(product2.getId()))
                                .quantity(1)
                                .build()
                ))
                .build();

        OrderDto orderDto2 = OrderDto.builder()
                .bankCardId("5431-1111-1111-1112")
                .price(19.99f)
                .entryDtos(Arrays.asList(
                        OrderEntryDto.builder()
                                .productId(productMapper.longToUuid(product1.getId()))
                                .quantity(1)
                                .build()
                ))
                .build();

        orderRepository.save(orderMapper.dtoToEntity(orderDto1));
        orderRepository.save(orderMapper.dtoToEntity(orderDto2));

        mockMvc.perform(get("/api/v1/orders/most-frequent-order-entries")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2)) // Expect two products
                .andExpect(jsonPath("$[0].productName").value(product1.getName())) // Either product1 or product2 can be first
                .andExpect(jsonPath("$[1].productName").value(product2.getName()));
    }
}
