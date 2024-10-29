package com.example.cosmocats.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CategoryDto {
    long id;
    String name;
}
