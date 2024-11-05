package com.example.cosmocats.dto.order;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderEntryDto {
    @NotNull(message = "Id is required")
    @Min(value = 1, message = "Id must be integer bigger than 0")
    Long productId;

    @Positive(message = "Quantity must be greater than 0")
    @Max(value = 100, message = "Cannot order more than 100 items of a single product")
    Integer quantity;
}
