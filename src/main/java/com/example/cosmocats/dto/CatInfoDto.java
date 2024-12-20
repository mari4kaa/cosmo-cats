package com.example.cosmocats.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CatInfoDto {
    @NotNull(message = "Id is required")
    UUID id;

    @NotBlank(message = "Cat name is required")
    @Size(min = 2, max = 50, message = "Cat name must be between 2 and 50 characters")
    String name;
}
