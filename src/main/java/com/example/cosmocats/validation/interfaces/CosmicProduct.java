package com.example.cosmocats.validation.interfaces;
import jakarta.validation.Payload;

@ContainsWord(value = { "cosmic", "space", "intergalactic",  })
public @interface CosmicProduct {
    String message() default "Field must contain one of \"cosmic words\"";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
