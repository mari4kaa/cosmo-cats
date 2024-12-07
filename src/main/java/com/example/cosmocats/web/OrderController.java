package com.example.cosmocats.web;

import com.example.cosmocats.dto.order.OrderDto;
import com.example.cosmocats.projection.ProductReport;
import com.example.cosmocats.service.OrderService;
import com.example.cosmocats.service.exception.*;
import com.example.cosmocats.service.exception.order.OrderCreationException;
import com.example.cosmocats.service.exception.order.OrderDeletionException;
import com.example.cosmocats.service.exception.order.OrderUpdateException;
import com.example.cosmocats.web.exception.OrderNotFoundException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@Slf4j
public class OrderController {

    private final OrderService orderService;
    
        public OrderController(OrderService orderService) {
            this.orderService = orderService;
        }
    
        @PostMapping
        public ResponseEntity<OrderDto> createOrder(@RequestBody @Valid OrderDto orderDto) {
            log.info("Creating order with ID");
            try {
                OrderDto createdOrder = orderService.createOrder(orderDto);
                return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
            } catch (OrderCreationException e) {
                log.error("Order creation failed: {}", e.getMessage());
                throw e;
            }
        }
    
        @GetMapping
        public ResponseEntity<List<OrderDto>> getAllOrders() {
            log.info("Fetching all orders");
            List<OrderDto> orders = orderService.getAllOrders();
            return ResponseEntity.ok(orders);
        }

        @GetMapping("/most-frequent-order-entries")
        public ResponseEntity<List<ProductReport>> getMostFrequentProducts() {
            List<ProductReport> reports = orderService.findMostFrequentOrderEntries();
            return ResponseEntity.ok(reports);
        }

        @GetMapping("/by-card/{bankCardId}")
        public ResponseEntity<List<OrderDto>> getAllOrdersByBankCardId(@PathVariable String bankCardId) {
            log.info("Fetching all orders");
            List<OrderDto> orders = orderService.getAllOrdersByBankCardId(bankCardId);
            return ResponseEntity.ok(orders);
        }
    
        @GetMapping("/{orderId}")
        public ResponseEntity<OrderDto> getOrderById(@PathVariable UUID orderId) {
            log.info("Fetching order with ID '{}'", orderId);
            return orderService.getOrderById(orderId)
                    .map(ResponseEntity::ok)
                    .orElseThrow(() -> new OrderNotFoundException("Order not found."));
        }

        @PutMapping("/{orderId}")
        public ResponseEntity<OrderDto> updateOrder(
                @PathVariable UUID orderId,
                @RequestBody @Valid OrderDto orderDto
        ) {
            log.info("Updating order with ID '{}'", orderId);
            try {
                OrderDto updatedOrder = orderService.updateOrder(orderId, orderDto);
                return ResponseEntity.ok(updatedOrder);
            } catch (OrderNotFoundException | OrderUpdateException e) {
                log.error("Failed to update order '{}': {}", orderId, e.getMessage());
                throw e;
            }
        }

        @DeleteMapping("/{orderId}")
        public ResponseEntity<Void> deleteOrder(@PathVariable UUID orderId) {
            log.info("Deleting order with ID '{}'", orderId);
            try {
                orderService.deleteOrder(orderId);
                return ResponseEntity.noContent().build();
            } catch (OrderNotFoundException | OrderDeletionException e) {
                log.error("Failed to delete order '{}': {}", orderId, e.getMessage());
                throw e;
            }
        }
}
