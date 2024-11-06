package com.example.cosmocats.dto.order;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderEntryDto {
    @NotNull(message = "Id is required")
    UUID productId;

    @Positive(message = "Quantity must be greater than 0")
    @Max(value = 100, message = "Cannot order more than 100 items of a single product")
    Integer quantity;
}
