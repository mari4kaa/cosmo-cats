package com.example.cosmocats.service.exception;

public class ProductUpdateException extends RuntimeException {
    public ProductUpdateException(String message) {
        super(message);
    }
}