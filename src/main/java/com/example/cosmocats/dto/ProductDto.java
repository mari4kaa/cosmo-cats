package com.example.cosmocats.dto;

import java.util.UUID;

import com.example.cosmocats.validation.interfaces.CosmicOrigin;
import com.example.cosmocats.validation.interfaces.CosmicProduct;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProductDto {
    UUID id;

    @NotNull(message = "Category id is required")
    UUID categoryId;

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    @CosmicProduct
    String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    String description;

    @NotBlank(message = "Origin planet is required")
    @Size(min = 2, max = 50, message = "Origin planet name must be between 2 and 50 characters")
    @CosmicOrigin
    String origin;

    @PositiveOrZero(message = "Price cannot be negative")
    Float price;
}
