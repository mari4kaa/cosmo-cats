package com.example.cosmocats.web.exception;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(String id) {
        super(String.format("Category not found with id: %s", id));
    }
}
