package com.example.cosmocats.validation.enums;

import java.util.Arrays;

public enum UserRoles {
    USER("user"),
    ADMIN("admin");

    private final String value;

    UserRoles(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String[] getValues() {
        return Arrays.stream(values()).map(UserRoles::getValue).toArray(String[]::new);
    }

    public static final String[] NAMES = getValues();
}