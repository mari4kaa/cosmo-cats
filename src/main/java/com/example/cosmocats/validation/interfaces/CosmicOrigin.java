package com.example.cosmocats.validation.interfaces;

import jakarta.validation.Payload;

@ContainsWord(value = { "Planet", "AnotherPlanet" })
public @interface CosmicOrigin  {
    String message() default "Origin must be some known planet";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
