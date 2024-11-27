package com.example.featuretoggle.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.example.featuretoggle.annotation.FeatureToggle;
import com.example.featuretoggle.exceptions.FeatureNotAvailableException;
import com.example.featuretoggle.service.FeatureToggleService;

@Aspect
@Component
public class FeatureToggleAspect {
    
    private final FeatureToggleService featureToggleService;

    public FeatureToggleAspect(FeatureToggleService featureToggleService) {
        this.featureToggleService = featureToggleService;
    }

    @Around("@annotation(featureToggle)")
    public Object checkFeatureToggle(ProceedingJoinPoint joinPoint, FeatureToggle featureToggle) throws Throwable {
        String featureName = featureToggle.value().getFeatureName();
        
        if (!featureToggleService.isFeatureEnabled(featureName)) {
            throw new FeatureNotAvailableException(featureName);
        }
        
        return joinPoint.proceed();
    }
}
