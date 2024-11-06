package com.example.cosmocats.domain;

import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Category {
    UUID id;
    String name;
}
