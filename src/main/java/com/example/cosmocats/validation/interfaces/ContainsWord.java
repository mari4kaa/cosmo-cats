package com.example.cosmocats.validation.interfaces;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.cosmocats.validation.validators.ContainsWordValidator;

@Constraint(validatedBy = ContainsWordValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ContainsWord {
    String[] value();
    String message() default "Will be overwritten";
    boolean ignoreCase() default true;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
