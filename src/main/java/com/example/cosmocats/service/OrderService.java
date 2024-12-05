package com.example.cosmocats.service;

import com.example.cosmocats.dto.order.OrderDto;
import com.example.cosmocats.entities.OrderEntity;
import com.example.cosmocats.mapper.OrderMapper;
import com.example.cosmocats.repository.OrderEntryRepository;
import com.example.cosmocats.repository.OrderRepository;
import com.example.cosmocats.service.exception.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
        this.orderMapper = OrderMapper.getInstance();
    }

    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        if (orderRepository.existsById(orderDto.getId().getMostSignificantBits())) {
            throw new OrderCreationException(String.format("Order with id '%s' already exists.", orderDto.getId()));
        }

        try {
            OrderEntity orderEntity = orderMapper.dtoToEntity(orderDto);
            OrderEntity savedEntity = orderRepository.save(orderEntity);
            log.info("Order created successfully with ID: {}", savedEntity.getId());
            return orderMapper.entityToDto(savedEntity);
        } catch (Exception e) {
            throw new OrderCreationException(String.format("Failed to create order: %s", e.getMessage()));
        }
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders() {
        log.info("Fetching all orders");
        return orderRepository.findAll().stream()
                .map(orderMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<OrderDto> getOrderById(UUID id) {
        log.info("Fetching order with ID '{}'", id);
        return orderRepository.findByNaturalId(id.getMostSignificantBits())
                .map(orderMapper::entityToDto);
    }

    @Transactional
    public OrderDto updateOrder(UUID id, OrderDto updatedOrderDto) {
        try {
            return orderRepository.findById(id.getMostSignificantBits())
                .map(existingEntity -> {
                    OrderEntity updatedEntity = orderMapper.dtoToEntity(updatedOrderDto);
                    updatedEntity.setId(id.getMostSignificantBits());
                    OrderEntity savedEntity = orderRepository.save(updatedEntity);
                    log.info("Order updated successfully with ID: {}", id);
                    return orderMapper.entityToDto(savedEntity);
                })
                .orElseThrow(() -> new OrderNotFoundException(String.format("Order with id '%d' not found", id.getMostSignificantBits())));
        } catch (Exception e) {
            throw new OrderUpdateException(String.format("Failed to update order: %s", e.getMessage()));
        }
    }

    @Transactional
    public void deleteOrder(UUID orderId) {
        if (orderRepository.findByNaturalId(orderId.getMostSignificantBits()).isPresent()) {
            return;
        }

        try {
            orderRepository.deleteByNaturalId(orderId.getMostSignificantBits());
        } catch (Exception e) {
            log.error("Failed to delete order '{}': {}", orderId, e.getMessage());
            throw new OrderDeletionException("Failed to delete order.");
        }
    }
}
