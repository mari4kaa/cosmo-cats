package com.example.cosmocats.validation.interfaces;
import com.example.cosmocats.validation.validators.ContainsWordValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = ContainsWordValidator.class)
@ContainsWord(value = { "cosmic", "space", "intergalactic",  })
public @interface CosmicProduct {
    String message() default "Field must contain one of \"cosmic words\"";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
