package com.example.cosmocats.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProductDto {
    @NotNull(message = "Id is required")
    @Min(value = 1, message = "Id must be integer bigger than 0")
    long id;
    @NotNull(message = "Category id is required")
    @Min(value = 1, message = "Category id must be integer bigger than 0")
    long categoryId;

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    // TODO: custom validation @CosmicWordCheck - name contains space terms
    String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    String description;

    @NotBlank(message = "Origin planet is required")
    @Size(min = 2, max = 50, message = "Origin planet name must be between 2 and 50 characters")
    // TODO: custom validation @OriginCheck - origin contains a planet name or galaxy name
    String origin;

    @PositiveOrZero(message = "Price cannot be negative")
    float price;
}
