package com.example.cosmocats.service;

import com.example.cosmocats.dto.ProductDto;
import com.example.cosmocats.entities.ProductEntity;
import com.example.cosmocats.mapper.ProductMapper;
import com.example.cosmocats.repository.ProductRepository;
import com.example.cosmocats.service.exception.ProductCreationException;
import com.example.cosmocats.service.exception.ProductUpdateException;
import com.example.cosmocats.web.exception.ProductNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductService productService;
    private ProductRepository productRepository; // Mocked
    private ProductMapper productMapper; // Real Mapper

    private ProductDto testProductDto;

    @BeforeEach
    void setUp() {
        // Initialize the real mapper
        productMapper = Mappers.getMapper(ProductMapper.class);

        // Mock the repository
        productRepository = mock(ProductRepository.class);

        // Create the service using real mapper and mocked repository
        productService = new ProductService(productRepository, productMapper);

        // Create a test ProductDto
        testProductDto = ProductDto.builder()
                .id(UUID.randomUUID())
                .categoryId(UUID.randomUUID())
                .name("CatStronaut Helmet")
                .description("A stellar helmet for adventurous cosmic cats.")
                .origin("Lunar Paw Station")
                .price(79.99f)
                .build();
    }

    @Test
    void createProduct_withValidData_shouldReturnCreatedProduct() {
        ProductEntity productEntity = productMapper.dtoToEntity(testProductDto);
        ProductEntity savedEntity = ProductEntity.builder()
                .id(1L)
                .category(productEntity.getCategory())
                .name(productEntity.getName())
                .description(productEntity.getDescription())
                .origin(productEntity.getOrigin())
                .price(productEntity.getPrice())
                .build();

        when(productRepository.save(any(ProductEntity.class))).thenReturn(savedEntity);

        ProductDto createdProduct = productService.createProduct(testProductDto);

        assertNotNull(createdProduct);
        assertEquals("CatStronaut Helmet", createdProduct.getName());
        assertEquals("Lunar Paw Station", createdProduct.getOrigin());
        assertEquals(79.99f, createdProduct.getPrice());
    }

    @Test
    void createProduct_withException_shouldThrowProductCreationException() {
        when(productRepository.save(any(ProductEntity.class))).thenThrow(RuntimeException.class);

        assertThrows(ProductCreationException.class, () -> productService.createProduct(testProductDto));
    }

    @Test
    void getAllProducts_whenProductsExist_shouldReturnProductList() {
        ProductEntity productEntity = productMapper.dtoToEntity(testProductDto);
        when(productRepository.findAll()).thenReturn(List.of(productEntity));

        List<ProductDto> products = productService.getAllProducts();

        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("CatStronaut Helmet", products.get(0).getName());
    }

    @Test
    void getAllProducts_whenNoProductsExist_shouldReturnEmptyList() {
        when(productRepository.findAll()).thenReturn(List.of());

        List<ProductDto> products = productService.getAllProducts();

        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    void getProductById_whenProductExists_shouldReturnProduct() {
        ProductEntity productEntity = productMapper.dtoToEntity(testProductDto);
        productEntity.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));

        Optional<ProductDto> retrievedProduct = productService.getProductById(1L);

        assertTrue(retrievedProduct.isPresent());
        assertEquals("CatStronaut Helmet", retrievedProduct.get().getName());
    }

    @Test
    void getProductById_whenProductDoesNotExist_shouldReturnEmptyOptional() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<ProductDto> retrievedProduct = productService.getProductById(1L);

        assertTrue(retrievedProduct.isEmpty());
    }

    @Test
    void updateProduct_whenProductExists_shouldUpdateAndReturnUpdatedProduct() {
        ProductEntity productEntity = productMapper.dtoToEntity(testProductDto);
        productEntity.setId(1L);

        ProductDto updatedProductDto = ProductDto.builder()
                .id(testProductDto.getId())
                .categoryId(testProductDto.getCategoryId())
                .name("Nebula Scratcher")
                .description("A scratching post designed for cosmic comfort.")
                .origin("Caturn")
                .price(59.99f)
                .build();

        ProductEntity updatedEntity = productMapper.dtoToEntity(updatedProductDto);
        updatedEntity.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(productEntity));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(updatedEntity);

        ProductDto result = productService.updateProduct(1L, updatedProductDto);

        assertNotNull(result);
        assertEquals("Nebula Scratcher", result.getName());
        assertEquals("Caturn", result.getOrigin());
        assertEquals(59.99f, result.getPrice());
    }

    @Test
    void updateProduct_withException_shouldThrowProductUpdateException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ProductUpdateException.class, () -> productService.updateProduct(1L, testProductDto));
    }

    @Test
    void deleteProduct_whenProductExists_shouldRemoveProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteProduct_whenProductDoesNotExist_shouldThrowNotFoundException() {
        when(productRepository.existsById(1L)).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(1L));
    }
}
