package com.example.featuretoggle;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.featuretoggle.service.FeatureToggleService;

@ExtendWith(MockitoExtension.class)
class FeatureToggleServiceTest {

    @Test
    void constructor_ShouldInitializeFeaturesCorrectly() {

        FeatureToggleService service = new FeatureToggleService(true, false);

        assertTrue(service.isFeatureEnabled(FeatureToggles.COSMO_CATS));
        assertFalse(service.isFeatureEnabled(FeatureToggles.KITTY_PRODUCTS));
    }

    @Test
    void enableFeature_ShouldEnableFeature() {

        FeatureToggleService service = new FeatureToggleService(false, false);
        
        service.enableFeature(FeatureToggles.COSMO_CATS);
        
        assertTrue(service.isFeatureEnabled(FeatureToggles.COSMO_CATS));
    }

    @Test
    void disableFeature_ShouldDisableFeature() {

        FeatureToggleService service = new FeatureToggleService(true, true);
        
        service.disableFeature(FeatureToggles.COSMO_CATS);

        assertFalse(service.isFeatureEnabled(FeatureToggles.COSMO_CATS));
    }

    @Test
    void isFeatureEnabled_WhenFeatureDoesNotExist_ShouldReturnFalse() {

        FeatureToggleService service = new FeatureToggleService(true, true);

        assertFalse(service.isFeatureEnabled("randomNonExistentFeature"));
    }
}
