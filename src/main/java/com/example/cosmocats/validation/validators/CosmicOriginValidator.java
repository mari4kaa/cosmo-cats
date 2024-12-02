package com.example.cosmocats.validation.validators;

import java.util.Arrays;
import java.util.List;

import com.example.cosmocats.validation.enums.CosmicOrigins;
import com.example.cosmocats.validation.interfaces.CosmicOrigin;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CosmicOriginValidator implements ConstraintValidator<CosmicOrigin, String> {
    private List<String> validOrigins;
    private String error;

    @Override
    public void initialize(CosmicOrigin constraintAnnotation) {
        this.validOrigins = Arrays.asList(CosmicOrigins.getValues());
        error = String.format("String should contain at least one of next words: %s", String.join(", ", validOrigins));
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean valid = validOrigins.stream().anyMatch(validOrigin -> validOrigin.equals(value));

        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    error).addConstraintViolation();
        }

        return valid;
    }
}
