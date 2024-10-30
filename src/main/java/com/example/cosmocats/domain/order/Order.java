package com.example.cosmocats.domain.order;

import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Order {
    long id;
    @Builder.Default
    List<OrderEntry> entries = java.util.Collections.emptyList();
    float price;
}
