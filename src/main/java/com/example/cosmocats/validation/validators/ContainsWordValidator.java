package com.example.cosmocats.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

import com.example.cosmocats.validation.interfaces.ContainsWord;

public class ContainsWordValidator implements ConstraintValidator<ContainsWord, String> {
    private List<String> words;

    @Override
    public void initialize(ContainsWord constraintAnnotation) {
        // Ініціалізація списку слів
        this.words = Arrays.asList(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        return words.stream().anyMatch(value::contains);
    }
}
