package com.example.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProductDto {
    long id;
    long categoryId;
    String name;
    String description;
    String origin;
    float price;
}
