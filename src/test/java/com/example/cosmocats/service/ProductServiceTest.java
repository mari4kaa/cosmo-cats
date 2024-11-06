package com.example.cosmocats.service;

import com.example.cosmocats.domain.Product;
import com.example.cosmocats.service.exception.ProductCreationException;
import com.example.cosmocats.service.exception.ProductUpdateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest {

    private ProductService productService;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        productService = new ProductService();
        testProduct = Product.builder()
                .id(UUID.randomUUID())
                .category(null)
                .name("CatStronaut Helmet")
                .description("A stellar helmet for adventurous cosmic cats.")
                .origin("Lunar Paw Station")
                .price(79.99f)
                .build();
    }

    @Test
    void createProduct_withValidData_shouldReturnCreatedProduct() {
        Product createdProduct = productService.createProduct(testProduct);
        
        assertNotNull(createdProduct);
        assertEquals("CatStronaut Helmet", createdProduct.getName());
        assertEquals("Lunar Paw Station", createdProduct.getOrigin());
        assertEquals(79.99f, createdProduct.getPrice());
        assertEquals(1L, createdProduct.getId());
    }

    @Test
    void createProduct_withException_shouldThrowProductCreationException() {
        Product invalidProduct = null;
        
        assertThrows(ProductCreationException.class, () -> productService.createProduct(invalidProduct));
    }

    @Test
    void getAllProducts_whenProductsExist_shouldReturnProductList() {
        productService.createProduct(testProduct);

        List<Product> products = productService.getAllProducts();
        
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("CatStronaut Helmet", products.get(0).getName());
    }

    @Test
    void getAllProducts_whenNoProductsExist_shouldReturnEmptyList() {
        List<Product> products = productService.getAllProducts();
        
        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    void getProductById_whenProductExists_shouldReturnProduct() {
        Product createdProduct = productService.createProduct(testProduct);
        
        Optional<Product> retrievedProduct = productService.getProductById(createdProduct.getId());
        
        assertTrue(retrievedProduct.isPresent());
        assertEquals("CatStronaut Helmet", retrievedProduct.get().getName());
    }

    @Test
    void getProductById_whenProductDoesNotExist_shouldReturnEmptyOptional() {
        Optional<Product> retrievedProduct = productService.getProductById(UUID.randomUUID());
        
        assertTrue(retrievedProduct.isEmpty());
    }

    @Test
    void updateProduct_whenProductExists_shouldUpdateAndReturnUpdatedProduct() {
        UUID id = UUID.randomUUID();
        Product updatedProduct = Product.builder()
                .id(id)
                .category(null)
                .name("Nebula Scratcher")
                .description("A scratching post designed for cosmic comfort.")
                .origin("Caturn")
                .price(59.99f)
                .build();
        
        Product newProduct = productService.updateProduct(id, updatedProduct);
        
        assertEquals(updatedProduct.getId(), newProduct.getId());
        assertEquals(updatedProduct, newProduct);
        assertEquals("Nebula Scratcher", newProduct.getName());
        assertEquals("Caturn", newProduct.getOrigin());
    }

    @Test
    void updateProduct_withException_shouldThrowProductUpdateException() {
        assertThrows(ProductUpdateException.class, () -> productService.updateProduct(UUID.randomUUID(), null));
    }

    @Test
    void deleteProduct_whenProductExists_shouldRemoveProduct() {
        Product createdProduct = productService.createProduct(testProduct);
        productService.deleteProduct(createdProduct.getId());
        
        Optional<Product> retrievedProduct = productService.getProductById(createdProduct.getId());
        assertTrue(retrievedProduct.isEmpty());
    }

    @Test
    void deleteProduct_whenProductDoesNotExist_shouldDoNothing() {
        productService.deleteProduct(UUID.randomUUID());
        assertTrue(productService.getAllProducts().isEmpty());
    }
}
