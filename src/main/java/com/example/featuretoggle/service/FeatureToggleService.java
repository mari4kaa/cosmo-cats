package com.example.featuretoggle.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.featuretoggle.FeatureToggles;

import java.util.HashMap;
import java.util.Map;

@Service
public class FeatureToggleService {
    
    private final Map<String, Boolean> features = new HashMap<>();

    public FeatureToggleService(
            @Value("${feature.cosmoCats.enabled:false}") boolean cosmoCatsEnabled,
            @Value("${feature.kittyProducts.enabled:false}") boolean kittyProductsEnabled
    ) {
        features.put(FeatureToggles.COSMO_CATS, cosmoCatsEnabled);
        features.put(FeatureToggles.KITTY_PRODUCTS, kittyProductsEnabled);
    }

    public boolean isFeatureEnabled(String featureName) {
        return features.getOrDefault(featureName, false);
    }

    public void enableFeature(String featureName) {
        features.put(featureName, true);
    }

    public void disableFeature(String featureName) {
        features.put(featureName, false);
    }
}