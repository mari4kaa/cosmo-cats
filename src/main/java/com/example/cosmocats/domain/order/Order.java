package com.example.cosmocats.domain.order;

import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Order {
    long id;
    List<OrderEntry> entries;
    float price;
}