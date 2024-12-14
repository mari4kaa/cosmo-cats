package com.example.cosmocats.domain.order;

import com.example.cosmocats.domain.Product;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class OrderEntry {
    Product product;
    int quantity;
}
