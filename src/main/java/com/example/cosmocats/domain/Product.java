package com.example.cosmocats.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Product {
    long id;
    Category category;
    String name;
    String description;
    String origin; // planet where was made
    float price;

}
