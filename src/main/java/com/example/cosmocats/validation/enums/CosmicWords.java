package com.example.cosmocats.validation.enums;

import java.util.Arrays;

public enum CosmicWords {
    COSMIC("cosmic"),
    SPACE("space"),
    INTERGALACTIC("intergalactic");

    private final String value;

    CosmicWords(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String[] getValues() {
        return Arrays.stream(values()).map(CosmicWords::getValue).toArray(String[]::new);
    }
}
