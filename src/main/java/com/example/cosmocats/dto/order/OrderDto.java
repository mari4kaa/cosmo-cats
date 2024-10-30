package com.example.cosmocats.dto.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderDto {
    long id;

    @NotEmpty(message = "Order must contain at least one entry")
    long[] entryIds;

    @PositiveOrZero(message = "Price cannot be negative")
    float price;
}
