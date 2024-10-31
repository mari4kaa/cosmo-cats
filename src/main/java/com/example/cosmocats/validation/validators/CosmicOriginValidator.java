package com.example.cosmocats.validation.validators;

import java.util.List;

import com.example.cosmocats.validation.interfaces.CosmicOrigin;

public class CosmicOriginValidator extends BaseContainsValidator<CosmicOrigin> {
    @Override
    public void initialize(CosmicOrigin constraintAnnotation) {
        this.words = List.of(
                // Solar System Planets
                "Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune",
                // Dwarf Planets
                "Pluto", "Ceres",
                // Fictional and Extra-Solar Planets
                "Terra", "Xandar", "Proxima", "Nibiru", "Erevos",
                "Celestis", "Draconis", "Kepler-442b", "Vega", "Rigel");
        
        this.sensitive = true;
    }
}
