package com.example.cosmocats.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cosmocats.dto.ProductDto;
import com.example.cosmocats.entities.ProductEntity;
import com.example.cosmocats.mapper.ProductMapper;
import com.example.cosmocats.repository.ProductRepository;
import com.example.cosmocats.service.exception.ProductCreationException;
import com.example.cosmocats.service.exception.ProductDeletionException;
import com.example.cosmocats.service.exception.ProductUpdateException;
import com.example.cosmocats.web.exception.ProductNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        if (productRepository.existsByName(productDto.getName())) {
            throw new ProductCreationException(String.format("Product with name '%s' already exists.", productDto.getName()));
        }

        try {
            ProductEntity productEntity = productMapper.dtoToEntity(productDto);
            ProductEntity savedEntity = productRepository.save(productEntity);
            log.info("Product created successfully with ID: {}", savedEntity.getId());
            return productMapper.entityToDto(savedEntity);
        } catch (Exception e) {
            throw new ProductCreationException(String.format("Failed to create product: %s", e.getMessage()));
        }
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
            .map(productMapper::entityToDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ProductDto> getProductById(UUID id) {
        return productRepository.findById(id.getMostSignificantBits())
            .map(productMapper::entityToDto);
    }

    @Transactional
    public ProductDto updateProduct(UUID id, ProductDto updatedProductDto) {
        try {
            return productRepository.findById(id.getMostSignificantBits())
                .map(existingEntity -> {
                    ProductEntity updatedEntity = productMapper.dtoToEntity(updatedProductDto);
                    updatedEntity.setId(id.getMostSignificantBits());
                    ProductEntity savedEntity = productRepository.save(updatedEntity);
                    log.info("Product updated successfully with ID: {}", id);
                    return productMapper.entityToDto(savedEntity);
                })
                .orElseThrow(() -> new ProductNotFoundException(String.format("Product with id '%d' not found", id.getMostSignificantBits())));
        } catch (Exception e) {
            throw new ProductUpdateException(String.format("Failed to update product: %s", e.getMessage()));
        }
    }

    @Transactional
    public void deleteProduct(UUID id) {
        if (!productRepository.existsById(id.getMostSignificantBits())) {
            return;
        }
        
        try {
            productRepository.deleteById(id.getMostSignificantBits());
            log.info("Product deleted successfully with ID: {}", id);
        } catch (Exception e) {
            log.error(String.format("Failed to delete product with ID %d: %s", id.getMostSignificantBits(), e.getMessage()));
            throw new ProductDeletionException(String.format("Failed to delete product: %s", e.getMessage()));
        }
    }
}
