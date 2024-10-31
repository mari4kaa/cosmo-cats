package com.example.cosmocats.validation.interfaces;

import com.example.cosmocats.validation.validators.ContainsWordValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = ContainsWordValidator.class)
@ContainsWord(value = {
        // Solar System Planets
        "Mercury", "Venus", "Earth", "Mars", "Jupiter", "Saturn", "Uranus", "Neptune",
        // Dwarf Planets
        "Pluto", "Ceres",
        // Fictional and Extra-Solar Planets
        "Terra", "Xandar", "Proxima", "Nibiru", "Erevos",
        "Celestis", "Draconis", "Kepler-442b", "Vega", "Rigel"
})
public @interface CosmicOrigin  {
    String message() default "Origin must be some known planet";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
