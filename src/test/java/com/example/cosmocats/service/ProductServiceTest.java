package com.example.cosmocats.service;

import com.example.cosmocats.dto.ProductDto;
import com.example.cosmocats.entities.ProductEntity;
import com.example.cosmocats.mapper.ProductMapper;
import com.example.cosmocats.repository.ProductRepository;
import com.example.cosmocats.service.exception.ProductCreationException;
import com.example.cosmocats.web.exception.ProductNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductService productService;
    private ProductRepository productRepository;
    private ProductMapper productMapper;

    private ProductDto testProductDto;

    private Long productId;
    private UUID id;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        productMapper = Mappers.getMapper(ProductMapper.class);

        productRepository = mock(ProductRepository.class);

        productService = new ProductService(productRepository, productMapper);

        id = UUID.randomUUID();
        productId = id.getMostSignificantBits();
        categoryId = UUID.randomUUID(); 

        testProductDto = ProductDto.builder()
                .id(id)
                .categoryId(categoryId)
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
                .id(productId)
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
        productEntity.setId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));

        Optional<ProductDto> retrievedProduct = productService.getProductById(id);

        assertTrue(retrievedProduct.isPresent());
        assertEquals("CatStronaut Helmet", retrievedProduct.get().getName());
    }

    @Test
    void getProductById_whenProductDoesNotExist_shouldReturnEmptyOptional() {
        UUID randId = UUID.randomUUID();
        Long randProductId = randId.getMostSignificantBits();

        when(productRepository.findById(randProductId)).thenReturn(Optional.empty());

        Optional<ProductDto> retrievedProduct = productService.getProductById(randId);

        assertTrue(retrievedProduct.isEmpty());
    }

    @Test
    void updateProduct_whenProductExists_shouldUpdateAndReturnUpdatedProduct() {
        ProductEntity productEntity = productMapper.dtoToEntity(testProductDto);
        productEntity.setId(productId);

        ProductDto updatedProductDto = ProductDto.builder()
                .id(testProductDto.getId())
                .categoryId(testProductDto.getCategoryId())
                .name("Nebula Scratcher")
                .description("A scratching post designed for cosmic comfort.")
                .origin("Caturn")
                .price(59.99f)
                .build();

        ProductEntity updatedEntity = productMapper.dtoToEntity(updatedProductDto);
        updatedEntity.setId(productId);

        when(productRepository.existsById(productId)).thenReturn(true);
        when(productRepository.findById(productId)).thenReturn(Optional.of(productEntity));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(updatedEntity);

        ProductDto result = productService.updateProduct(testProductDto.getId(), updatedProductDto);

        assertNotNull(result);
        assertEquals("Nebula Scratcher", result.getName());
        assertEquals("Caturn", result.getOrigin());
        assertEquals(59.99f, result.getPrice());
    }

    @Test
    void updateProduct_withException_shouldThrowProductUpdateException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(testProductDto.getId(), testProductDto));
    }

    @Test
    void deleteProduct_whenProductExists_shouldRemoveProduct() {
        when(productRepository.existsById(productId)).thenReturn(true);

        productService.deleteProduct(testProductDto.getId());

        verify(productRepository, times(1)).deleteById(productId);
    }
}
