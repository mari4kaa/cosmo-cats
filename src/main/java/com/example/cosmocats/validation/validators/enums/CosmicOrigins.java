package com.example.cosmocats.validation.validators.enums;

import java.util.Arrays;

// Пакет: validations.enums
public enum CosmicOrigins {
    MERCURY("Mercury"),
    VENUS("Venus"),
    EARTH("Earth"),
    MARS("Mars"),
    JUPITER("Jupiter"),
    SATURN("Saturn"),
    URANUS("Uranus"),
    NEPTUNE("Neptune"),
    
    // Карликові планети
    PLUTO("Pluto"),
    CERES("Ceres"),

    // Вигадані та позасонячні планети
    TERRA("Terra"),
    XANDAR("Xandar"),
    PROXIMA("Proxima"),
    NIBIRU("Nibiru"),
    EREVOS("Erevos"),
    CELESTIS("Celestis"),
    DRACONIS("Draconis"),
    KEPLER_442B("Kepler-442b"),
    VEGA("Vega"),
    RIGEL("Rigel");

    private final String value;

    CosmicOrigins(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String[] getValues() {
        return Arrays.stream(values()).map(CosmicOrigins::getValue).toArray(String[]::new);
    }
}

