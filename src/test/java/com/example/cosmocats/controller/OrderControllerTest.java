package com.example.cosmocats.controller;

import com.example.cosmocats.dto.order.OrderDto;
import com.example.cosmocats.projection.ProductReport;
import com.example.cosmocats.service.OrderService;
import com.example.cosmocats.web.OrderController;
import com.example.cosmocats.web.exception.OrderNotFoundException;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    /*@Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderDto validOrderDto;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        validOrderDto = OrderDto.builder()
                .id(orderId)
                .entryIds(List.of(UUID.randomUUID()))
                .price(50.0f)
                .build();
    }

    @Test
    @SneakyThrows
    void createOrder_withValidData_shouldReturn201() {
        Mockito.when(orderService.createOrder(Mockito.any(OrderDto.class))).thenReturn(validOrderDto);

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validOrderDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.price").value(50.0f));
    }

    @Test
    @SneakyThrows
    void getOrderById_withValidId_shouldReturnOrder() {
        Mockito.when(orderService.getOrderById(orderId)).thenReturn(Optional.of(validOrderDto));

        mockMvc.perform(get("/api/v1/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()));
    }

    @Test
    @SneakyThrows
    void getOrderById_withInvalidId_shouldReturn404() {
        Mockito.when(orderService.getOrderById(Mockito.any(UUID.class))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/orders/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void getAllOrders_shouldReturnOrderList() {
        Mockito.when(orderService.getAllOrders()).thenReturn(List.of(validOrderDto));

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getMostFrequentProducts_shouldReturnListOfProducts() throws Exception {
        List<ProductReport> mockReports = List.of(
            new TestProductReport("Cosmic Yarn", 25L),
            new TestProductReport("Stellar Helmet", 20L)
        );

        when(orderService.findMostFrequentOrderEntries()).thenReturn(mockReports);

        mockMvc.perform(get("/api/v1/orders/most-frequent-order-entries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].productName").value("Cosmic Yarn"))
                .andExpect(jsonPath("$[0].totalQuantity").value(25))
                .andExpect(jsonPath("$[1].productName").value("Stellar Helmet"))
                .andExpect(jsonPath("$[1].totalQuantity").value(20));
    }


    @Test
    void getMostFrequentProducts_whenNoData_shouldReturnEmptyList() throws Exception {

        when(orderService.findMostFrequentOrderEntries()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/orders/most-frequent-order-entries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @SneakyThrows
    void updateOrder_withValidData_shouldReturnUpdatedOrder() {
        UUID randId = UUID.randomUUID();
        OrderDto updatedOrderDto = OrderDto.builder()
                .id(randId)
                .entryIds(List.of(UUID.randomUUID()))
                .price(150.0f)
                .build();

        when(orderService.updateOrder(Mockito.eq(randId), Mockito.any(OrderDto.class))).thenReturn(updatedOrderDto);

        mockMvc.perform(put("/api/v1/orders/{orderId}", randId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedOrderDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(randId.toString()))
                .andExpect(jsonPath("$.price").value(150.0f));
    }

    @Test
    @SneakyThrows
    void updateOrder_withInvalidOrderId_shouldReturn404() {
        UUID randId = UUID.randomUUID();
        OrderDto updatedOrderDto = OrderDto.builder()
                .id(randId)
                .entryIds(List.of(UUID.randomUUID()))
                .price(150.0f)
                .build();

        when(orderService.updateOrder(Mockito.eq(randId), Mockito.any(OrderDto.class))).thenThrow(new OrderNotFoundException("Order not found"));

        mockMvc.perform(put("/api/v1/orders/{orderId}", randId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedOrderDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void deleteOrder_withValidId_shouldReturn204() {
        Mockito.doNothing().when(orderService).deleteOrder(orderId);

        mockMvc.perform(delete("/api/v1/orders/{id}", orderId))
                .andExpect(status().isNoContent());
    }
}

class TestProductReport implements ProductReport {
    private final String productName;
    private final long totalQuantity;

    public TestProductReport(String productName, long totalQuantity) {
        this.productName = productName;
        this.totalQuantity = totalQuantity;
    }

    @Override
    public String getProductName() {
        return productName;
    }

    @Override
    public Long getTotalQuantity() {
        return totalQuantity;
    }*/
}
