package com.example.cosmocats.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.cosmocats.domain.Product;
import com.example.cosmocats.service.exception.ProductCreationException;
import com.example.cosmocats.service.exception.ProductUpdateException;

@Service
public class ProductService {
    private final Map<UUID, Product> products = new HashMap<>();

    public Product createProduct(Product product) {
        try {
            Product newProduct = Product.builder()
                    .id(UUID.randomUUID())
                    .category(product.getCategory())
                    .name(product.getName())
                    .description(product.getDescription())
                    .origin(product.getOrigin())
                    .price(product.getPrice())
                    .build();
            products.put(newProduct.getId(), newProduct);
            return newProduct;
        } catch (Exception e) {
            throw new ProductCreationException("Failed to create product: " + e.getMessage());
        }
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    public Optional<Product> getProductById(UUID id) {
        return Optional.ofNullable(products.get(id));
    }

    public Product updateProduct(UUID id, Product updatedProduct) {
        try {
            Product product = Product.builder()
                    .id(id)
                    .category(updatedProduct.getCategory())
                    .name(updatedProduct.getName())
                    .description(updatedProduct.getDescription())
                    .origin(updatedProduct.getOrigin())
                    .price(updatedProduct.getPrice())
                    .build();

            products.put(id, product);

           return product;
        } catch (Exception e) {
            throw new ProductUpdateException("Failed to update product: " + e.getMessage());
        }
    }

    public void deleteProduct(UUID id) {
        products.remove(id);
    }
}