package com.example.featuretoggle.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "feature.toggles")
public class FeatureTogglesConfig {
    private Map<String, Boolean> toggles = new HashMap<>();

    public Map<String, Boolean> getToggles() {
        return toggles;
    }

    public void setToggles(Map<String, Boolean> toggles) {
        this.toggles = toggles;
    }
}
