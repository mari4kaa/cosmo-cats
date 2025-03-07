package com.example.cosmocats.controller;

import com.example.cosmocats.config.noauth.NoAuthSecurityConfiguration;
import com.example.cosmocats.domain.Category;
import com.example.cosmocats.dto.ProductDto;
import com.example.cosmocats.service.ProductService;
import com.example.cosmocats.validation.enums.CosmicOrigins;
import com.example.cosmocats.validation.enums.CosmicWords;
import com.example.cosmocats.web.ProductController;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ActiveProfiles("no-auth")
@WebMvcTest(ProductController.class)
@Import({NoAuthSecurityConfiguration.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDto validProductDto;

    private UUID productUUID;
    private UUID categoryUUID;

    @BeforeEach
    void setUp() {

        productUUID = UUID.randomUUID();
        categoryUUID = UUID.randomUUID();

        Category category = Category.builder()
                .id(categoryUUID)
                .name("Galaxy Goods")
                .build();

        validProductDto = ProductDto.builder()
                .id(productUUID)
                .categoryId(category.getId())
                .name("Cosmic Beam")
                .description("A cosmic product for stellar journeys.")
                .origin("Terra")
                .price(199.99f)
                .build();
    }

    @Test
    @SneakyThrows
    void getProductById_withValidId_shouldReturnProduct() {
        Mockito.when(productService.getProductById(productUUID)).thenReturn(Optional.of(validProductDto));

        mockMvc.perform(get(String.format("/api/v1/admin/products/%s", productUUID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cosmic Beam"))
                .andExpect(jsonPath("$.origin").value("Terra"))
                .andExpect(jsonPath("$.price").value(199.99));
    }

    @Test
    @SneakyThrows
    void getProductById_withNonExistentId_shouldReturn404() {
        UUID randUUID = UUID.randomUUID();
        Mockito.when(productService.getProductById(randUUID)).thenReturn(Optional.empty());

        mockMvc.perform(get(String.format("/api/v1/admin/products/%s", randUUID)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value(String.format("Product not found with id: %s", randUUID)));
    }

    @Test
    @SneakyThrows
    void getAllProducts_shouldReturnProductList() {
        Mockito.when(productService.getAllProducts()).thenReturn(List.of(validProductDto));

        mockMvc.perform(get("/api/v1/admin/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Cosmic Beam"));
    }

    @Test
    @SneakyThrows
    void createProduct_withValidData_shouldReturn200() {
        Mockito.when(productService.createProduct(Mockito.any(ProductDto.class))).thenReturn(validProductDto);

        mockMvc.perform(post("/api/v1/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validProductDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Cosmic Beam"))
                .andExpect(jsonPath("$.origin").value("Terra"))
                .andExpect(jsonPath("$.price").value(199.99));
    }

    @Test
    @SneakyThrows
    void createProduct_withInvalidName_shouldReturn400() {
        ProductDto invalidProduct = ProductDto.builder()
                .id(productUUID)
                .categoryId(categoryUUID)
                .name("") // Invalid: name cannot be blank, must be between 2 and 100 characters and
                          // contain a cosmic word
                .description("A cosmic product for stellar journeys.")
                .origin("Terra")
                .price(199.99f)
                .build();

        mockMvc.perform(post("/api/v1/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.detail")
                        .value(org.hamcrest.Matchers.containsString(
                                "name: Product should contain at least one of cosmic words: "
                                        + String.join(", ", CosmicWords.getValues()))))
                .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers
                        .containsString("name: Product name must be between 2 and 100 characters")))
                .andExpect(jsonPath("$.detail")
                        .value(org.hamcrest.Matchers.containsString("name: Product name is required")))
                .andExpect(jsonPath("$.instance").value("uri=/api/v1/admin/products"));
    }

    @Test
    @SneakyThrows
    void createProduct_withInvalidOrigin_shouldReturn400() {
        ProductDto invalidProduct = ProductDto.builder()
                .id(productUUID)
                .categoryId(categoryUUID)
                .name("Intergalactic Jet")
                .description("A cosmic product for stellar journeys.")
                .origin("UnknownPlanet") // Invalid: origin must be a known planet
                .price(199.99f)
                .build();

        mockMvc.perform(post("/api/v1/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.detail")
                        .value("Validation failed: origin: String should contain at least one of next words: "
                                + String.join(", ", CosmicOrigins.getValues())))
                .andExpect(jsonPath("$.instance").value("uri=/api/v1/admin/products"));
    }

    @Test
    @SneakyThrows
    void createProduct_withNegativePrice_shouldReturn400() {
        ProductDto invalidProduct = ProductDto.builder()
                .id(productUUID)
                .categoryId(categoryUUID)
                .name("Space Orb")
                .description("An interstellar item.")
                .origin("Terra")
                .price(-50.0f) // Invalid: price cannot be negative
                .build();

        mockMvc.perform(post("/api/v1/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.detail").value("Validation failed: price: Price cannot be negative"))
                .andExpect(jsonPath("$.instance").value("uri=/api/v1/admin/products"));
    }

    @Test
    @SneakyThrows
    void createProduct_withTooLongDescription_shouldReturn400() {
        ProductDto invalidProduct = ProductDto.builder()
                .id(productUUID)
                .categoryId(categoryUUID)
                .name("Intergalactic Shard")
                .description("S".repeat(501)) // Invalid: description cannot exceed 500 characters
                .origin("Terra")
                .price(150.0f)
                .build();

        mockMvc.perform(post("/api/v1/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.detail")
                        .value("Validation failed: description: Description cannot exceed 500 characters"))
                .andExpect(jsonPath("$.instance").value("uri=/api/v1/admin/products"));
    }

    @Test
    @SneakyThrows
    void updateProduct_withValidUpdatedData_shouldReturn200() {
        Mockito.when(productService.updateProduct(Mockito.eq(productUUID), Mockito.any())).thenReturn(validProductDto);
        mockMvc.perform(put(String.format("/api/v1/admin/products/%s", productUUID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validProductDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Cosmic Beam"));
    }

    @Test
    @SneakyThrows
    void updateOrCreateProduct_withInvalidCategoryId_shouldReturn400() {
        ProductDto invalidProduct = ProductDto.builder()
                .id(productUUID)
                .categoryId(null) // Invalid: categoryId cannot be null
                .name("Cosmic Yarn")
                .description("Perfect yarn for cosmo cats.")
                .origin("Terra")
                .price(120.0f)
                .build();

        mockMvc.perform(put(String.format("/api/v1/admin/products/%s", productUUID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.detail").value("Validation failed: categoryId: Category id is required"))
                .andExpect(jsonPath("$.instance").value(String.format("uri=/api/v1/admin/products/%s", productUUID)));
    }

    @Test
    @SneakyThrows
    void deleteProduct_withValidId_shouldReturn204() {
        Mockito.doNothing().when(productService).deleteProduct(productUUID);

        mockMvc.perform(delete(String.format("/api/v1/admin/products/%s", productUUID)))
                .andExpect(status().isNoContent());
    }

    @Test
    @SneakyThrows
    void deleteProduct_withNonExistentId_shouldReturn204() {
        UUID randUUID = UUID.randomUUID();
        Mockito.doNothing().when(productService).deleteProduct(randUUID);

        mockMvc.perform(delete(String.format("/api/v1/admin/products/%s", randUUID)))
                .andExpect(status().isNoContent());
    }
}
