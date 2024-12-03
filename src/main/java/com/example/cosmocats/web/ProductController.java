package com.example.cosmocats.web;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.cosmocats.web.exception.ProductNotFoundException;
import com.example.cosmocats.dto.ProductDto;
import com.example.cosmocats.service.ProductService;
import com.example.cosmocats.service.exception.ProductCreationException;
import com.example.cosmocats.service.exception.ProductDeletionException;
import com.example.cosmocats.service.exception.ProductUpdateException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Validated
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto) {
        try {
            log.info("Attempting to create a new product: {}", productDto);
            ProductDto createdProductDto = productService.createProduct(productDto);
            
            log.info("Product created successfully with ID: {}", createdProductDto.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProductDto);
        } catch (ProductCreationException e) {
            log.error("Error creating product: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        try {
            log.info("Fetching all products");
            List<ProductDto> productDtos = productService.getAllProducts();
            
            log.info("Retrieved {} products", productDtos.size());
            return ResponseEntity.ok(productDtos);
        } catch (Exception e) {
            log.error("Error retrieving products: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable UUID id) {
        try {
            log.info("Fetching product with ID: {}", id);
            ProductDto productDto = productService.getProductById(id.getMostSignificantBits())
                .orElseThrow(() -> new ProductNotFoundException(id.toString()));
            
            log.info("Product found: {}", productDto);
            return ResponseEntity.ok(productDto);
        } catch (ProductNotFoundException e) {
            log.warn("Product not found with ID: {}", id);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable UUID id, 
            @Valid @RequestBody ProductDto productDto) {
        try {
            log.info("Attempting to update product with ID: {}", id);
            
            ProductDto updatedProductDto = productService.updateProduct(id.getMostSignificantBits(), productDto);
            
            log.info("Product updated successfully: {}", updatedProductDto);
            return ResponseEntity.ok(updatedProductDto);
        } catch (ProductUpdateException | ProductNotFoundException e) {
            log.error("Error updating product with ID {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        try {
            log.info("Attempting to delete product with ID: {}", id);
            productService.deleteProduct(id.getMostSignificantBits());
            
            log.info("Product deleted successfully with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (ProductDeletionException e) {
            log.error("Error deleting product with ID {}: {}", id, e.getMessage());
            throw e;
        }
    }
}