package com.example.cosmocats.web.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String id) {
        super(String.format("Order not found with id: %s", id));
    }
}
