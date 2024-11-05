package com.example.cosmocats.validation.validators;

import java.util.Arrays;
import java.util.List;
import com.example.cosmocats.validation.interfaces.CosmicOrigin;
import com.example.cosmocats.validation.validators.enums.CosmicOrigins;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CosmicOriginValidator implements ConstraintValidator<CosmicOrigin, String> {
    private List<String> validOrigins;

    @Override
    public void initialize(CosmicOrigin constraintAnnotation) {
        this.validOrigins = Arrays.asList(CosmicOrigins.getValues());
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return validOrigins.stream().anyMatch(validOrigin -> validOrigin.equals(value));
    }
}
