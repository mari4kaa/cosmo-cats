package com.example.cosmocats.domain;

import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Product {
    UUID id;
    Category category;
    String name;
    String description;
    String origin; // planet where was made
    float price;

}
