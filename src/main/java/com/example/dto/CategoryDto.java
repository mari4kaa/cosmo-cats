package com.example.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CategoryDto {
    long id;
    String name;
}
