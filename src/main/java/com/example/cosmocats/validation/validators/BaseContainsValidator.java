package com.example.cosmocats.validation.validators;

import java.lang.annotation.Annotation;
import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public abstract class BaseContainsValidator<T extends Annotation> implements ConstraintValidator<T, String> {
    protected List<String> words;
    protected boolean ignoreCase;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return words.stream().anyMatch(word -> ignoreCase ? insensitiveMatch(word, value) : sensitiveMatch(word, value));
    }

    private boolean sensitiveMatch(String word, String value) {
        return value.contains(word);
    }

    private boolean insensitiveMatch(String word, String value) {
        return value.toLowerCase().contains(word.toLowerCase());
    }
}
