package com.example.cosmocats.featuretoggle;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.featuretoggle.config.FeatureTogglesConfig;
import com.example.featuretoggle.service.FeatureToggleService;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class FeatureToggleServiceTest {
    
    @Mock
    private FeatureTogglesConfig featureTogglesConfig;
    
    private FeatureToggleService featureToggleService;
    
    @BeforeEach
    void setUp() {
        when(featureTogglesConfig.getToggles()).thenReturn(new HashMap<>());
        featureToggleService = new FeatureToggleService(featureTogglesConfig);
    }
    
    @Test
    void constructor_ShouldInitializeFeaturesCorrectly() {
        Map<String, Boolean> testToggles = new HashMap<>();
        testToggles.put("cosmoCats", true);
        testToggles.put("kittyProducts", false);
        
        when(featureTogglesConfig.getToggles()).thenReturn(testToggles);
        
        FeatureToggleService service = new FeatureToggleService(featureTogglesConfig);
        
        assertTrue(service.isFeatureEnabled("cosmoCats"));
        assertFalse(service.isFeatureEnabled("kittyProducts"));
    }
    
    @Test
    void enableFeature_ShouldEnableFeature() {
        featureToggleService.enableFeature("cosmoCats");
        
        assertTrue(featureToggleService.isFeatureEnabled("cosmoCats"));
    }
    
    @Test
    void disableFeature_ShouldDisableFeature() {
        featureToggleService.enableFeature("cosmoCats");
        featureToggleService.disableFeature("cosmoCats");
        
        assertFalse(featureToggleService.isFeatureEnabled("cosmoCats"));
    }
    
    @Test
    void isFeatureEnabled_WhenFeatureDoesNotExist_ShouldReturnFalse() {
        assertFalse(featureToggleService.isFeatureEnabled("randomNonExistentFeature"));
    }
    
    @Test
    void isFeatureEnabled_WhenConfigIsEmpty_ShouldReturnFalse() {
        assertFalse(featureToggleService.isFeatureEnabled("anyFeature"));
    }
}