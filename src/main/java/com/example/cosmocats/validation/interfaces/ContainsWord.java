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
    boolean ignoreCase() default true;
    String message() default "Field must contain one of the specified words";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
