package com.example.cosmocats.dto.order;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderEntryDto {
    long productId;
    int quantity;
}
