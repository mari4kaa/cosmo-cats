package com.example.cosmocats.featuretoggle;

import lombok.Getter;

@Getter
public enum FeatureToggles {
    COSMO_CATS("cosmoCats"),
    KITTY_PRODUCTS("kittyProducts");
    
    private final String featureName;

    FeatureToggles(String featureName) {
        this.featureName = featureName;
    }
}
