package com.example.domain;

import java.util.List;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Order {
    long id;
    List<Product> products; // TODO: maybe use []
    float price;
}