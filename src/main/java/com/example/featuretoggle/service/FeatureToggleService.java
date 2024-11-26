package com.example.featuretoggle.service;

import org.springframework.stereotype.Service;

import com.example.featuretoggle.config.FeatureTogglesConfig;

import java.util.HashMap;
import java.util.Map;

@Service
public class FeatureToggleService {

    private final Map<String, Boolean> features = new HashMap<>();

    public FeatureToggleService(FeatureTogglesConfig featureTogglesConfig) {
        this.features.putAll(featureTogglesConfig.getToggles());
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
