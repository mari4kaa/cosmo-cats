package com.example.cosmocats.validation.interfaces;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import com.example.cosmocats.validation.validators.CosmicOriginValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = CosmicOriginValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CosmicOrigin  {
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
