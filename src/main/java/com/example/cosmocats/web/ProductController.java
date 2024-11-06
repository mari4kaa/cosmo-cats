package com.example.cosmocats.web;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.cosmocats.web.exception.ProductNotFoundException;
import com.example.cosmocats.domain.Product;
import com.example.cosmocats.dto.ProductDto;
import com.example.cosmocats.mapper.ProductMapper;
import com.example.cosmocats.service.ProductService;

import jakarta.validation.Valid;

@RestController
@Validated
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper = ProductMapper.INSTANCE;

    public ProductController (ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody @Valid ProductDto productDto) {
        Product product = productMapper.dtoToProduct(productDto);
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.ok(
                productMapper.productToDto(createdProduct)
        );
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductDto> productDtos = products.stream()
                .map(productMapper::productToDto)
                .toList();
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable UUID id) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ResponseEntity.ok(productMapper.productToDto(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateOrCreateProduct(
        @PathVariable UUID id,
        @Valid @RequestBody ProductDto productDto) {

    Product product = productMapper.dtoToProduct(productDto);
    Product updatedProduct = productService.updateProduct(id, product);
    
    return new ResponseEntity<>(productMapper.productToDto(updatedProduct), HttpStatus.OK);
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}