package com.example.cosmocats.domain.order;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Order {
    UUID id;
    List<OrderEntry> entries;
    float price;
    String bankCardId;
}
