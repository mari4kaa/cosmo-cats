package com.example.cosmocats.validation.validators;

import java.util.List;
import com.example.cosmocats.validation.interfaces.CosmicOrigin;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CosmicOriginValidator implements ConstraintValidator<CosmicOrigin, String> {
    private List<String> validOrigins;

    @Override
    public void initialize(CosmicOrigin constraintAnnotation) {
        this.validOrigins = List.of(
                // Solar System Planets
                "Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune",
                // Dwarf Planets
                "Pluto", "Ceres",
                // Fictional and Extra-Solar Planets
                "Terra", "Xandar", "Proxima", "Nibiru", "Erevos",
                "Celestis", "Draconis", "Kepler-442b", "Vega", "Rigel");
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return validOrigins.stream().anyMatch(validOrigin -> validOrigin.equals(value));
    }
}
