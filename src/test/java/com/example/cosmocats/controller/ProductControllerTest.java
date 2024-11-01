package com.example.cosmocats.controller;

import com.example.cosmocats.domain.Category;
import com.example.cosmocats.domain.Product;
import com.example.cosmocats.dto.ProductDto;
import com.example.cosmocats.service.ProductService;
import com.example.cosmocats.web.ProductController;
import com.example.cosmocats.web.exception.ProductNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDto validProductDto;
    private Product validProduct;

    @BeforeEach
    void setUp() {
        Category category = Category.builder()
                .id(10L)
                .name("Galaxy Goods")
                .build();

        validProductDto = ProductDto.builder()
                .id(1L)
                .categoryId(category.getId())
                .name("Cosmic Beam")
                .description("A cosmic product for stellar journeys.")
                .origin("Terra")
                .price(199.99f)
                .build();

        validProduct = Product.builder()
                .id(1L)
                .category(category)
                .name("Cosmic Beam")
                .description("A cosmic product for stellar journeys.")
                .origin("Terra")
                .price(199.99f)
                .build();
    }

    @Test
    void getProductById_withValidId_shouldReturnProduct() throws Exception {
        Mockito.when(productService.getProductById(1L)).thenReturn(Optional.of(validProduct));

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cosmic Beam"))
                .andExpect(jsonPath("$.origin").value("Terra"))
                .andExpect(jsonPath("$.price").value(199.99));
    }

    @Test
    void getProductById_withNonExistentId_shouldReturn404() throws Exception {
        Mockito.when(productService.getProductById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product not found with id: 999"));
    }

    @Test
    void getAllProducts_shouldReturnProductList() throws Exception {
        Mockito.when(productService.getAllProducts()).thenReturn(List.of(validProduct));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Cosmic Beam"));
    }
    
    @Test
    void createProduct_withValidData_shouldReturn200() throws Exception {
        Mockito.when(productService.createProduct(Mockito.any(Product.class))).thenReturn(validProduct);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validProductDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cosmic Beam"))
                .andExpect(jsonPath("$.origin").value("Terra"))
                .andExpect(jsonPath("$.price").value(199.99));
    }

    @Test
    void updateOrCreateProduct_withValidUpdatedData_shouldReturn200() throws Exception {
        Mockito.when(productService.updateProduct(Mockito.anyLong(), Mockito.any())).thenReturn(false); // Updated product
        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validProductDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cosmic Beam"));
    }

    @Test
    void updateOrCreateProduct_withValidCreatedData_shouldReturn201() throws Exception {
        Mockito.when(productService.updateProduct(Mockito.anyLong(), Mockito.any())).thenReturn(true); // Created product
        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validProductDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Cosmic Beam"));
    }

    @Test
    void createProduct_withInvalidName_shouldReturn400() throws Exception {
    ProductDto invalidProduct = ProductDto.builder()
        .id(1L)
        .categoryId(10L)
        .name("")  // Invalid: name cannot be blank, must be between 2 and 100 characters and contain a cosmic word
        .description("A cosmic product for stellar journeys.")
        .origin("Terra")
        .price(199.99f)
        .build();

    mockMvc.perform(post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidProduct)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("name: Field must contain one of 'cosmic words'")))
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("name: Product name must be between 2 and 100 characters")))
            .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("name: Product name is required")))
            .andExpect(jsonPath("$.path").value("uri=/api/v1/products"));
}

    @Test
    void createProduct_withInvalidOrigin_shouldReturn400() throws Exception {
        ProductDto invalidProduct = ProductDto.builder()
            .id(1L)
            .categoryId(10L)
            .name("Intergalactic Jet")
            .description("A cosmic product for stellar journeys.")
            .origin("UnknownPlanet")  // Invalid: origin must be a known planet
            .price(199.99f)
            .build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("Validation failed: , origin: Origin must be some known planet"))
                .andExpect(jsonPath("$.path").value("uri=/api/v1/products"));
    }

    @Test
    void createProduct_withNegativePrice_shouldReturn400() throws Exception {
        ProductDto invalidProduct = ProductDto.builder()
            .id(1L)
            .categoryId(10L)
            .name("Space Orb")
            .description("An interstellar item.")
            .origin("Terra")
            .price(-50.0f)  // Invalid: price cannot be negative
            .build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("Validation failed: , price: Price cannot be negative"))
                .andExpect(jsonPath("$.path").value("uri=/api/v1/products"));
    }

    @Test
    void createProduct_withTooLongDescription_shouldReturn400() throws Exception {
        ProductDto invalidProduct = ProductDto.builder()
            .id(1L)
            .categoryId(10L)
            .name("Intergalactic Shard")
            .description("S".repeat(501))  // Invalid: description cannot exceed 500 characters
            .origin("Terra")
            .price(150.0f)
            .build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("Validation failed: , description: Description cannot exceed 500 characters"))
                .andExpect(jsonPath("$.path").value("uri=/api/v1/products"));
    }

    @Test
    void updateOrCreateProduct_withInvalidCategoryId_shouldReturn400() throws Exception {
        ProductDto invalidProduct = ProductDto.builder()
            .id(1L)
            .categoryId(-1L)  // Invalid: categoryId cannot be negative
            .name("Cosmic Yarn")
            .description("Perfect yarn for cosmo cats.")
            .origin("Terra")
            .price(120.0f)
            .build();

        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("Validation failed: , categoryId: Category ID cannot be negative"))
                .andExpect(jsonPath("$.path").value("uri=/api/v1/products/1"));
    }

    @Test
    void deleteProduct_withValidId_shouldReturn204() throws Exception {
        Mockito.doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProduct_withNonExistentId_shouldReturn204() throws Exception {
        Mockito.doNothing().when(productService).deleteProduct(999L);

        mockMvc.perform(delete("/api/v1/products/999"))
                .andExpect(status().isNoContent());
    }
}



