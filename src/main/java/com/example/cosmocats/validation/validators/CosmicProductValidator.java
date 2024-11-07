package com.example.cosmocats.validation.validators;

import java.util.Arrays;

import com.example.cosmocats.validation.enums.CosmicWords;
import com.example.cosmocats.validation.interfaces.CosmicProduct;

public class CosmicProductValidator extends BaseContainsValidator<CosmicProduct> {
    @Override
    public void initialize(CosmicProduct constraintAnnotation) {
        this.words = Arrays.asList(CosmicWords.getValues());
        this.ignoreCase = true;
        this.error = "Product should contain at least one of cosmic words: " + String.join(", ", words);
    }
}
