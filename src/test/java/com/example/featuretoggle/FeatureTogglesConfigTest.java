package com.example.featuretoggle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.featuretoggle.config.FeatureTogglesConfig;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

class FeatureTogglesConfigTest {

    private FeatureTogglesConfig featureTogglesConfig;

    @BeforeEach
    void setUp() {
        featureTogglesConfig = new FeatureTogglesConfig();
    }

    @Test
    void constructor_ShouldCreateEmptyTogglesMap() {
        assertTrue(featureTogglesConfig.getToggles().isEmpty());
    }

    @Test
    void setToggles_ShouldUpdateTogglesMap() {
        Map<String, Boolean> testToggles = new HashMap<>();
        testToggles.put("cosmoCats", true);
        testToggles.put("kittyProducts", false);

        featureTogglesConfig.setToggles(testToggles);

        assertEquals(testToggles, featureTogglesConfig.getToggles());
        assertTrue(featureTogglesConfig.getToggles().get("cosmoCats"));
        assertFalse(featureTogglesConfig.getToggles().get("kittyProducts"));
    }

    @Test
    void getToggles_ShouldReturnCurrentTogglesMap() {
        Map<String, Boolean> testToggles = new HashMap<>();
        testToggles.put("newFeature", true);

        featureTogglesConfig.setToggles(testToggles);

        assertEquals(testToggles, featureTogglesConfig.getToggles());
    }

    @Test
    void toggles_ShouldBeMutable() {
        featureTogglesConfig.getToggles().put("dynamicFeature", true);
        
        assertTrue(featureTogglesConfig.getToggles().containsKey("dynamicFeature"));
        assertTrue(featureTogglesConfig.getToggles().get("dynamicFeature"));
    }
}
