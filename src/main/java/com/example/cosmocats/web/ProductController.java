package com.example.cosmocats.web;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;

    @PreAuthorize("hasRole('IMPORTANT_CAT')")
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto) {
        try {
            log.info("Creating a new product: {}", productDto);
            ProductDto createdProductDto = productService.createProduct(productDto);
            
            log.info("Product created successfully with ID: {}", createdProductDto.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProductDto);
        } catch (ProductCreationException e) {
            log.error("Error creating product: {}", e.getMessage());
            throw e;
        }
    }

    @PreAuthorize("hasRole('BASIC_CAT')")
    @GetMapping
        public ResponseEntity<List<ProductDto>> getAllProducts() {
            log.info("Fetching all products");
            List<ProductDto> products = productService.getAllProducts();
            return ResponseEntity.ok(products);
        }

    @PreAuthorize("hasRole('BASIC_CAT')")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable UUID id) {
        log.info("Fetching product with ID '{}'", id);
        return productService.getProductById(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ProductNotFoundException(id.toString()));
    }

    @PreAuthorize("hasRole('IMPORTANT_CAT')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable UUID id, 
            @Valid @RequestBody ProductDto productDto) {
        try {
            log.info("Updating product with ID: {}", id);
            
            ProductDto updatedProductDto = productService.updateProduct(id, productDto);
            
            log.info("Product updated successfully: {}", updatedProductDto);
            return ResponseEntity.ok(updatedProductDto);
        } catch (ProductUpdateException | ProductNotFoundException e) {
            log.error("Error updating product with ID {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @PreAuthorize("hasRole('IMPORTANT_CAT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        try {
            log.info("Deleting product with ID: {}", id);
            productService.deleteProduct(id);
            
            log.info("Product deleted successfully with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (ProductDeletionException e) {
            log.error("Error deleting product with ID {}: {}", id, e.getMessage());
            throw e;
        }
    }
}