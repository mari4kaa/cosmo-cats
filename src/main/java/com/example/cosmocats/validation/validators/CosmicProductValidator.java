package com.example.cosmocats.validation.validators;

import java.util.Arrays;

import com.example.cosmocats.validation.interfaces.CosmicProduct;
import com.example.cosmocats.validation.validators.enums.CosmicWords;

public class CosmicProductValidator extends BaseContainsValidator<CosmicProduct> {
    @Override
    public void initialize(CosmicProduct constraintAnnotation) {
        this.words = Arrays.asList(CosmicWords.getValues());
        this.ignoreCase = true;
    }
}
