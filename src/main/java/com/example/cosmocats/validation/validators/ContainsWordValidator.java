package com.example.cosmocats.validation.validators;

import java.util.Arrays;

import com.example.cosmocats.validation.interfaces.ContainsWord;

public class ContainsWordValidator extends BaseContainsValidator<ContainsWord> {
    @Override
    public void initialize(ContainsWord constraintAnnotation) {
        this.words = Arrays.asList(constraintAnnotation.value());
        this.sensitive = constraintAnnotation.sensitive();
    }
}
