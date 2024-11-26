package com.example.cosmocats.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.support.AopUtils;

import com.example.featuretoggle.exceptions.FeatureNotAvailableException;
import com.example.cosmocats.domain.CatInfo;
import com.example.cosmocats.dto.CatInfoDto;
import com.example.featuretoggle.FeatureToggles;
import com.example.featuretoggle.aspect.FeatureToggleAspect;
import com.example.featuretoggle.service.FeatureToggleService;

@ExtendWith(MockitoExtension.class)
class CosmoCatServiceTest {

    @Mock
    private FeatureToggleService featureToggleService;

    private CosmoCatService proxiedCosmoCatService;

    @BeforeEach
    void setUp() {
        CosmoCatService targetService = new CosmoCatService();
        FeatureToggleAspect aspect = new FeatureToggleAspect(featureToggleService);

        AspectJProxyFactory factory = new AspectJProxyFactory(targetService);
        factory.addAspect(aspect);

        // Get the proxied service
        proxiedCosmoCatService = factory.getProxy();

        // Verify that we actually created a proxy
        assertTrue(AopUtils.isAopProxy(proxiedCosmoCatService));
    }

    @Test
    void getCosmoCats_WhenFeatureEnabled_ShouldReturnCats() {
        when(featureToggleService.isFeatureEnabled(FeatureToggles.COSMO_CATS))
                .thenReturn(true);

        try {

            List<CatInfoDto> result = proxiedCosmoCatService.getCosmoCats();

            assertNotNull(result);
            assertFalse(result.isEmpty());

            assertTrue(
                    result.stream().anyMatch(cat -> "Cosmo Cat".equals(cat.getName())),
                    "Expected 'Space Cat' in the result list");
            assertTrue(
                    result.stream().anyMatch(cat -> "Astro Cat".equals(cat.getName())),
                    "Expected 'Galaxy Cat' in the result list");
            assertTrue(
                    result.stream().anyMatch(cat -> "Star Cat".equals(cat.getName())),
                    "Expected 'Star Cat' in the result list");

            verify(featureToggleService).isFeatureEnabled(FeatureToggles.COSMO_CATS);

        } catch (FeatureNotAvailableException e) {
            fail(String.format("Exception should not be thrown when feature is enabled: %s", e.getMessage()));
        }
    }

    @Test
    void getCosmoCats_WhenFeatureDisabled_ShouldThrowException() {

        when(featureToggleService.isFeatureEnabled(FeatureToggles.COSMO_CATS))
                .thenReturn(false);

        FeatureNotAvailableException exception = assertThrows(
                FeatureNotAvailableException.class,
                () -> proxiedCosmoCatService.getCosmoCats());

        assertEquals("Feature cosmoCats is not available", exception.getMessage());
        verify(featureToggleService).isFeatureEnabled(FeatureToggles.COSMO_CATS);
    }
}
