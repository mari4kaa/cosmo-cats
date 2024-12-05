package com.example.cosmocats.dto.order;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderDto {
    UUID id;

    @NotEmpty(message = "Order must contain at least one entry")
    List<UUID> entryIds;

    @PositiveOrZero(message = "Price cannot be negative")
    Float price;
}
