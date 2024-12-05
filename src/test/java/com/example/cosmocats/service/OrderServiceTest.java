package com.example.cosmocats.service;

import com.example.cosmocats.dto.ProductDto;
import com.example.cosmocats.dto.order.OrderDto;
import com.example.cosmocats.entities.CategoryEntity;
import com.example.cosmocats.entities.OrderEntity;
import com.example.cosmocats.mapper.OrderMapper;
import com.example.cosmocats.projection.ProductReport;
import com.example.cosmocats.repository.OrderEntryRepository;
import com.example.cosmocats.repository.OrderRepository;
import com.example.cosmocats.service.exception.OrderCreationException;
import com.example.cosmocats.service.exception.OrderUpdateException;
import com.example.cosmocats.web.exception.OrderNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderService orderService;
    private OrderRepository orderRepository;
    private OrderEntryRepository orderEntryRepository;
    private OrderMapper orderMapper;

    private UUID id;
    private Long orderId;
    private OrderDto testOrderDto;

    @BeforeEach
    void setUp() {
        orderMapper = Mappers.getMapper(OrderMapper.class);
        orderRepository = mock(OrderRepository.class);
        orderEntryRepository = mock(OrderEntryRepository.class);
        orderService = new OrderService(orderRepository, orderMapper, orderEntryRepository);

        id = UUID.randomUUID();
        orderId = id.getMostSignificantBits();

        testOrderDto = OrderDto.builder()
                .id(id)
                .entryIds(List.of(UUID.randomUUID()))
                .price(99.99f)
                .build();
    }

    @Test
    void createOrder_withValidData_shouldReturnCreatedOrder() {
        OrderEntity testOrderEntity = orderMapper.dtoToEntity(testOrderDto);

        when(orderRepository.save(Mockito.any(OrderEntity.class))).thenReturn(testOrderEntity);

        OrderDto createdOrder = orderService.createOrder(testOrderDto);

        assertNotNull(createdOrder);
        assertEquals(99.99f, createdOrder.getPrice());
    }

    @Test
    void getOrderById_whenOrderExists_shouldReturnOrder() {
        OrderEntity testOrderEntity = orderMapper.dtoToEntity(testOrderDto);
        testOrderEntity.setId(orderId);

        when(orderRepository.findByNaturalId(orderId)).thenReturn(Optional.of(testOrderEntity));

        Optional<OrderDto> retrievedOrder = orderService.getOrderById(testOrderDto.getId());

        assertTrue(retrievedOrder.isPresent());
        assertEquals(99.99f, retrievedOrder.get().getPrice());
    }

    @Test
    void getOrderById_whenOrderDoesNotExist_shouldReturnEmptyOptional() {
        UUID randUUID = UUID.randomUUID();
        Long randId = randUUID.getMostSignificantBits();

        when(orderRepository.findByNaturalId(randId)).thenReturn(Optional.empty());

        Optional<OrderDto> retrievedProduct = orderService.getOrderById(randUUID);

        assertTrue(retrievedProduct.isEmpty());
    }

    @Test
    void getAllOrders_shouldReturnOrderList() {
        OrderEntity testOrderEntity = orderMapper.dtoToEntity(testOrderDto);

        when(orderRepository.findAll()).thenReturn(List.of(testOrderEntity));

        List<OrderDto> orders = orderService.getAllOrders();

        assertNotNull(orders);
        assertEquals(1, orders.size());
    }

    @Test
    void findMostFrequentOrderEntries_shouldReturnMostFrequentProducts() {
        ProductReport mockReport1 = Mockito.mock(ProductReport.class);
        Mockito.when(mockReport1.getProductName()).thenReturn("Cosmic Yarn");
        Mockito.when(mockReport1.getTotalQuantity()).thenReturn(25L);

        ProductReport mockReport2 = Mockito.mock(ProductReport.class);
        Mockito.when(mockReport2.getProductName()).thenReturn("Stellar Helmet");
        Mockito.when(mockReport2.getTotalQuantity()).thenReturn(20L);

        List<ProductReport> mockReports = List.of(mockReport1, mockReport2);

        when(orderEntryRepository.findMostFrequentlyBoughtProducts()).thenReturn(mockReports);

        List<ProductReport> reports = orderService.findMostFrequentOrderEntries();

        assertEquals(2, reports.size());
        assertEquals("Cosmic Yarn", reports.get(0).getProductName());
        assertEquals(25, reports.get(0).getTotalQuantity());
        verify(orderEntryRepository, times(1)).findMostFrequentlyBoughtProducts();
    }

    @Test
    void findMostFrequentOrderEntries_whenNoData_shouldReturnEmptyList() {

        when(orderEntryRepository.findMostFrequentlyBoughtProducts()).thenReturn(List.of());

        List<ProductReport> reports = orderService.findMostFrequentOrderEntries();

        assertEquals(0, reports.size());
        verify(orderEntryRepository, times(1)).findMostFrequentlyBoughtProducts();
    }

    @Test
    void updateOrder_withValidData_shouldUpdateOrder() {
        OrderEntity testOrderEntity = orderMapper.dtoToEntity(testOrderDto);
        testOrderEntity.setId(orderId);

        OrderDto updatedOrderDto = OrderDto.builder()
                .entryIds(List.of(UUID.randomUUID()))
                .price(150.0f)
                .build();

        OrderEntity updatedOrderEntity = orderMapper.dtoToEntity(updatedOrderDto);
        updatedOrderEntity.setId(orderId);

        when(orderRepository.existsById(orderId)).thenReturn(true);
        when(orderRepository.findByNaturalId(orderId)).thenReturn(Optional.of(testOrderEntity));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(updatedOrderEntity);

        OrderDto result = orderService.updateOrder(testOrderDto.getId(), updatedOrderDto);

        assertNotNull(result);
        assertEquals(150.0f, result.getPrice());
    }

    @Test
    void updateOrder_whenOrderDoesNotExist_shouldThrowOrderNotFoundException() {
        UUID newId = UUID.randomUUID();
        OrderDto updatedOrderDto = OrderDto.builder()
                .id(newId)
                .entryIds(List.of(UUID.randomUUID()))
                .price(150.0f)
                .build();

        when(orderRepository.findByNaturalId(newId.getMostSignificantBits())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.updateOrder(newId, updatedOrderDto));
    }

    @Test
    void updateOrder_withException_shouldThrowOrderUpdateException() {
        UUID newId = UUID.randomUUID();
        when(orderRepository.existsById(newId.getMostSignificantBits())).thenReturn(true);
        when(orderRepository.findByNaturalId(newId.getMostSignificantBits())).thenThrow(new RuntimeException("Database error"));

        assertThrows(OrderUpdateException.class, () -> orderService.updateOrder(newId, testOrderDto));
    }


    @Test
    void deleteOrder_whenOrderExists_shouldCallRepositoryDelete() {
        OrderEntity testOrderEntity = orderMapper.dtoToEntity(testOrderDto);
        testOrderEntity.setId(orderId);

        when(orderRepository.findByNaturalId(orderId)).thenReturn(Optional.of(testOrderEntity));

        orderService.deleteOrder(testOrderDto.getId());

        verify(orderRepository, times(1)).deleteByNaturalId(orderId);
    }
}
