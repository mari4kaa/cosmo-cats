package com.example.featuretoggle.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
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

    @Around("@annotation(com.example.cosmocats.featureToggle.FeatureToggle)")
    public Object checkFeatureToggle(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        FeatureToggle featureToggle = signature.getMethod().getAnnotation(FeatureToggle.class);
        
        if (!featureToggleService.isFeatureEnabled(featureToggle.feature())) {
            throw new FeatureNotAvailableException(
                "Feature " + featureToggle.feature() + " is not available"
            );
        }
        
        return joinPoint.proceed();
    }
}