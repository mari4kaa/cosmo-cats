package com.example.cosmocats.service.exception;

public class ProductDeletionException extends RuntimeException {
    public ProductDeletionException(String message) {
        super(message);
    }
}