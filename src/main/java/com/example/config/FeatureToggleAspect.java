package com.example.config;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.example.config.exceptions.FeatureNotAvailableException;

@Aspect
@Component
public class FeatureToggleAspect {
    private final FeatureToggleService featureToggleService;

    public FeatureToggleAspect(FeatureToggleService service) {
        this.featureToggleService = service;
    }

    @Before("@annotation(com.example.annotation.RequiresCosmoCatsFeature)")
    public void checkCosmoCatsFeatureEnabled() throws FeatureNotAvailableException {
        if (featureToggleService.isCosmoCats()) return;
        throw new FeatureNotAvailableException("cosmoCats");
    }

    @Before("@annotation(com.example.annotation.RequiresKittyProductsFeature)")
    public void checkKittyProductsFeatureEnabled() throws FeatureNotAvailableException {
        if (featureToggleService.isKittyProducts()) return;
        throw new FeatureNotAvailableException("kittyProducts");
    }
}
