package com.example.cosmocats.validation.validators;

import java.util.Arrays;

import com.example.cosmocats.validation.interfaces.ContainsWord;

public class ContainsWordValidator extends BaseContainsValidator<ContainsWord> {
    @Override
    public void initialize(ContainsWord constraintAnnotation) {
        words = Arrays.asList(constraintAnnotation.value());
        ignoreCase = constraintAnnotation.ignoreCase();
        error = "String should contain at least one of next words: " + String.join(", ", words);
    }
}
