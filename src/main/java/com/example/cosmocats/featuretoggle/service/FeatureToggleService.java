package com.example.cosmocats.featuretoggle.service;

import org.springframework.stereotype.Service;

import com.example.cosmocats.featuretoggle.config.FeatureTogglesConfig;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class FeatureToggleService {

    private final ConcurrentHashMap<String, Boolean> features;

    public FeatureToggleService(FeatureTogglesConfig featureTogglesConfig) {
        features = new ConcurrentHashMap<>(featureTogglesConfig.getToggles());
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
