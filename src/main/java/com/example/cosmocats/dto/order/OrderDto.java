package com.example.cosmocats.dto.order;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderDto {
    long id;
    long[] entryIds;
    float price;
}
