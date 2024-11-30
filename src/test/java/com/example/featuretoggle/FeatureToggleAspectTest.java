package com.example.featuretoggle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import com.example.cosmocats.featuretoggle.FeatureToggles;
import com.example.cosmocats.featuretoggle.annotation.FeatureToggle;
import com.example.cosmocats.featuretoggle.aspect.FeatureToggleAspect;
import com.example.cosmocats.featuretoggle.exceptions.FeatureNotAvailableException;
import com.example.cosmocats.featuretoggle.service.FeatureToggleService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeatureToggleAspectTest {

    private FeatureToggleAspect featureToggleAspect;
    private FeatureToggleService featureToggleService;

    private TestService testServiceProxy;

    @BeforeEach
    void setup() {

        featureToggleService = mock(FeatureToggleService.class);
        featureToggleAspect = new FeatureToggleAspect(featureToggleService);

        TestService testService = new TestService();
        AspectJProxyFactory proxyFactory = new AspectJProxyFactory(testService);
        proxyFactory.addAspect(featureToggleAspect);
        testServiceProxy = proxyFactory.getProxy();
    }

    @Test
    void shouldAllowMethodExecutionWhenFeatureIsEnabled() {

        when(featureToggleService.isFeatureEnabled(FeatureToggles.COSMO_CATS.getFeatureName())).thenReturn(true);

        String result = testServiceProxy.getCosmoCatsFeature();

        assertEquals("Feature executed", result);
        verify(featureToggleService, times(1))
                .isFeatureEnabled(FeatureToggles.COSMO_CATS.getFeatureName());
    }

    @Test
    void shouldBlockMethodExecutionWhenFeatureIsDisabled() {

        when(featureToggleService.isFeatureEnabled(FeatureToggles.COSMO_CATS.getFeatureName())).thenReturn(false);

        FeatureNotAvailableException exception = assertThrows(FeatureNotAvailableException.class, () -> {
            testServiceProxy.getCosmoCatsFeature();
        });

        assertEquals("Feature cosmoCats is not available", exception.getMessage());
    }

    static class TestService {
        @FeatureToggle(FeatureToggles.COSMO_CATS)
        public String getCosmoCatsFeature() {
            return "Feature executed";
        }
    }
}
