package com.example.cosmocats.service;

import com.example.cosmocats.domain.Product;
import com.example.cosmocats.service.exception.ProductCreationException;
import com.example.cosmocats.service.exception.ProductUpdateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest {

    private ProductService productService;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        productService = new ProductService();
        testProduct = Product.builder()
                .id(1L)
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
        Optional<Product> retrievedProduct = productService.getProductById(999L);
        
        assertTrue(retrievedProduct.isEmpty());
    }

    @Test
    void updateProduct_whenProductExists_shouldUpdateAndReturnFalse() {
        Product createdProduct = productService.createProduct(testProduct);
        
        Product updatedProduct = Product.builder()
                .id(createdProduct.getId())
                .category(null)
                .name("Nebula Scratcher")
                .description("A scratching post designed for cosmic comfort.")
                .origin("Caturn")  // Cat-themed name based on Saturn
                .price(59.99f)
                .build();
        
        boolean isCreated = productService.updateProduct(createdProduct.getId(), updatedProduct);
        
        assertFalse(isCreated);
        Optional<Product> retrievedProduct = productService.getProductById(createdProduct.getId());
        assertTrue(retrievedProduct.isPresent());
        assertEquals("Nebula Scratcher", retrievedProduct.get().getName());
        assertEquals("Caturn", retrievedProduct.get().getOrigin());
    }

    @Test
    void updateProduct_whenProductDoesNotExist_shouldCreateAndReturnTrue() {
        Product newProduct = Product.builder()
                .id(2L)
                .category(null)
                .name("Comet Kibble")
                .description("A treat for interstellar cats on a journey through the stars.")
                .origin("Andromeda Treat Center")
                .price(9.99f)
                .build();
        
        boolean isCreated = productService.updateProduct(2L, newProduct);
        
        assertTrue(isCreated);
        Optional<Product> retrievedProduct = productService.getProductById(2L);
        assertTrue(retrievedProduct.isPresent());
        assertEquals("Comet Kibble", retrievedProduct.get().getName());
    }

    @Test
    void updateProduct_withException_shouldThrowProductUpdateException() {
        assertThrows(ProductUpdateException.class, () -> productService.updateProduct(1L, null));
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
        productService.deleteProduct(999L);
        assertTrue(productService.getAllProducts().isEmpty());
    }
}
