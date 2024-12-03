package com.example.cosmocats.service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
            throw new ProductCreationException(String.format("Product with name %s already exists.", productDto.getName()));
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
    public Optional<ProductDto> getProductById(Long id) {
        return productRepository.findById(id)
            .map(productMapper::entityToDto);
    }

    @Transactional
    public ProductDto updateProduct(Long id, ProductDto updatedProductDto) {
        try {
            return productRepository.findById(id)
                .map(existingEntity -> {
                    ProductEntity updatedEntity = productMapper.dtoToEntity(updatedProductDto);
                    updatedEntity.setId(id);

                    ProductEntity savedEntity = productRepository.save(updatedEntity);
                    
                    log.info("Product updated successfully with ID: {}", id);

                    return productMapper.entityToDto(savedEntity);
                })
                .orElseThrow(() -> new ProductNotFoundException("Product with id " + id + " not found"));
        } catch (Exception e) {
            throw new ProductUpdateException(String.format("Failed to update product: %s", e.getMessage()));
        }
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            log.error("Attempt to delete non-existent product with ID: {}", id);
            throw new ProductNotFoundException("Product with id " + id + " not found");
        }
        
        try {
            productRepository.deleteById(id);
            log.info("Product deleted successfully with ID: {}", id);
        } catch (Exception e) {
            log.error("Failed to delete product with ID {}: {}", id, e.getMessage());
            throw new ProductDeletionException("Failed to delete product: " + e.getMessage());
        }
    }
}
