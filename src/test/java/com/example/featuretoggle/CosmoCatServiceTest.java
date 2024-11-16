package com.example.featuretoggle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import com.example.featuretoggle.exceptions.FeatureNotAvailableException;
import com.example.featuretoggle.aspect.FeatureToggleAspect;
import com.example.featuretoggle.service.FeatureToggleService;
import com.example.cosmocats.service.CosmoCatService;

@ExtendWith(MockitoExtension.class)
class CosmoCatServiceTest {

    @Mock
    private FeatureToggleService featureToggleService;

    private CosmoCatService cosmoCatService;
    private CosmoCatService proxiedCosmoCatService;

    @BeforeEach
    void setUp() {
        cosmoCatService = new CosmoCatService();
        
        AspectJProxyFactory factory = new AspectJProxyFactory(cosmoCatService);
        FeatureToggleAspect aspect = new FeatureToggleAspect(featureToggleService);
        factory.addAspect(aspect);
        
        proxiedCosmoCatService = factory.getProxy();
    }

    @Test
    void getCosmoCats_WhenFeatureEnabled_ShouldReturnCats() {
        
        when(featureToggleService.isFeatureEnabled(FeatureToggles.COSMO_CATS))
            .thenReturn(true);

        List<String> result = proxiedCosmoCatService.getCosmoCats();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("Space Cat"));
        assertTrue(result.contains("Galaxy Cat"));
        assertTrue(result.contains("Star Cat"));
        
        verify(featureToggleService).isFeatureEnabled(FeatureToggles.COSMO_CATS);
    }

    @Test
    void getCosmoCats_WhenFeatureDisabled_ShouldThrowException() {

        when(featureToggleService.isFeatureEnabled(FeatureToggles.COSMO_CATS))
            .thenReturn(false);

        assertThrows(FeatureNotAvailableException.class, 
            () -> proxiedCosmoCatService.getCosmoCats());
        
        verify(featureToggleService).isFeatureEnabled(FeatureToggles.COSMO_CATS);
    }
}
