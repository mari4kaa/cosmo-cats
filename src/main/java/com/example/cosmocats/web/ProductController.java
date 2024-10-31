package com.example.cosmocats.web;

import java.util.List;

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
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ResponseEntity.ok(productMapper.productToDto(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateOrCreateProduct(
        @PathVariable Long id,
        @Valid @RequestBody ProductDto productDto) {

    Product product = productMapper.dtoToProduct(productDto);
    boolean isCreated = productService.updateProduct(id, product);
    
    // Determine the response status based on whether the product was created or updated
    if (isCreated) {
        return new ResponseEntity<>(productMapper.productToDto(product), HttpStatus.CREATED);
    } else {
        return new ResponseEntity<>(productMapper.productToDto(product), HttpStatus.OK);
    }
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}