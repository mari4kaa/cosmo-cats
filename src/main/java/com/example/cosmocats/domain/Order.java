package com.example.cosmocats.domain;

import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Order {
    long id;
    List<Product> products;
    float price;
}