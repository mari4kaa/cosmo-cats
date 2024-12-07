package com.example.cosmocats.service;

import com.example.cosmocats.dto.order.OrderDto;
import com.example.cosmocats.entities.OrderEntity;
import com.example.cosmocats.mapper.OrderMapper;
import com.example.cosmocats.projection.ProductReport;
import com.example.cosmocats.repository.OrderEntryRepository;
import com.example.cosmocats.repository.OrderRepository;
import com.example.cosmocats.service.exception.*;
import com.example.cosmocats.web.exception.CategoryNotFoundException;
import com.example.cosmocats.web.exception.OrderNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderEntryRepository orderEntryRepository;
    
    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        OrderEntity orderEntity = orderMapper.dtoToEntity(orderDto);
        OrderEntity savedEntity = orderRepository.save(orderEntity);
        log.info("Order created successfully with ID: {}", savedEntity.getId());
        return orderMapper.entityToDto(savedEntity);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders() {
        log.info("Fetching all orders");
        return orderRepository.findAll().stream()
                .map(orderMapper::entityToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List <ProductReport> findMostFrequentOrderEntries() {
        return orderEntryRepository.findMostFrequentlyBoughtProducts();
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrdersByBankCardId(String bankCardId) {
        log.info("Fetching all orders for Bank Card ID: {}", bankCardId);
        return orderRepository.findByBankCardId(bankCardId).stream()
                .map(orderMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<OrderDto> getOrderById(UUID id) {
        log.info("Fetching order with ID '{}'", id);
        return orderRepository.findById(orderMapper.uuidToLong(id))
                .map(orderMapper::entityToDto);
    }

    @Transactional
    public OrderDto updateOrder(UUID id, OrderDto updatedOrderDto) {
        OrderEntity existingEntity = orderRepository.findById(orderMapper.uuidToLong(id))
            .orElseThrow(() -> new OrderNotFoundException(id.toString()));

        if(!Objects.equals(updatedOrderDto.getBankCardId(), existingEntity.getBankCardId())) {
            throw new OrderUpdateException("The bank card ID cannot be changed in the order");
        }

        try {
            orderMapper.updateEntityFromDto(updatedOrderDto, existingEntity);
            existingEntity.setId(orderMapper.uuidToLong(id));
            OrderEntity savedEntity = orderRepository.save(existingEntity);
            log.info("Order updated successfully with ID: {}", id);
            return orderMapper.entityToDto(savedEntity);
        } catch (Exception e) {
            throw new OrderUpdateException(String.format("Failed to update order: %s", e.getMessage()));
        }
    }

    @Transactional
    public void deleteOrder(UUID id) {
        if (!orderRepository.findById(orderMapper.uuidToLong(id)).isPresent()) {
            return;
        }

        try {
            orderRepository.deleteById(id.getMostSignificantBits());
        } catch (Exception e) {
            log.error("Failed to delete order '{}': {}", id, e.getMessage());
            throw new OrderDeletionException("Failed to delete order.");
        }
    }
}
