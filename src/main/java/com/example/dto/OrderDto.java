package com.example.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OrderDto {
    long id;
    long[] productIds;
    float price;
}
