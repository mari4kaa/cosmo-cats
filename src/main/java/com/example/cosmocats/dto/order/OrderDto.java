package com.example.cosmocats.dto.order;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderDto {
    @NotNull(message = "Id is required")
    @Min(value = 1, message = "Id must be integer bigger than 0")
    Long id;

    @NotEmpty(message = "Order must contain at least one entry")
    List<Long> entryIds;

    @PositiveOrZero(message = "Price cannot be negative")
    Float price;
}
