package com.example.cosmocats.validation.validators;

import java.util.List;

import com.example.cosmocats.validation.interfaces.CosmicProduct;

public class CosmicProductValidator extends BaseContainsValidator<CosmicProduct> {
    @Override
    public void initialize(CosmicProduct constraintAnnotation) {
        this.words = List.of("cosmic", "space", "intergalactic");
        this.sensitive = false;
    }
}
