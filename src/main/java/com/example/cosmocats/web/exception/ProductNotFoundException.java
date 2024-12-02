package com.example.cosmocats.web.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(UUID id) {
        super(String.format("Product not found with id: %s", id));
    }
}
