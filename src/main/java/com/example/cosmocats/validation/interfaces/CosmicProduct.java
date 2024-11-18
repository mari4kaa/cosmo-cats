package com.example.cosmocats.validation.interfaces;

import com.example.cosmocats.validation.validators.CosmicProductValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CosmicProductValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CosmicProduct {
    String message() default "Will be overwritten";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}