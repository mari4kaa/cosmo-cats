package com.example.featuretoggle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.featuretoggle.annotation.FeatureToggle;
import com.example.featuretoggle.aspect.FeatureToggleAspect;
import com.example.featuretoggle.exceptions.FeatureNotAvailableException;
import com.example.featuretoggle.service.FeatureToggleService;

import java.lang.reflect.Method;

@ExtendWith(MockitoExtension.class)
class FeatureToggleAspectTest {

    @Mock
    private FeatureToggleService featureToggleService;

    @Mock
    private ProceedingJoinPoint joinPoint;

    private FeatureToggleAspect aspect;

    @BeforeEach
    void setUp() {
        aspect = new FeatureToggleAspect(featureToggleService);
    }

    @Test
    void checkFeatureToggle_WhenFeatureEnabled_ShouldProceed() throws Throwable {

        Method method = TestService.class.getMethod("testMethod");
        when(joinPoint.getSignature()).thenReturn(new TestMethodSignature(method));
        when(featureToggleService.isFeatureEnabled(FeatureToggles.COSMO_CATS)).thenReturn(true);
        when(joinPoint.proceed()).thenReturn("success");

        Object result = aspect.checkFeatureToggle(joinPoint);

        assertEquals("success", result);
        verify(joinPoint).proceed();
        verify(featureToggleService).isFeatureEnabled(FeatureToggles.COSMO_CATS);
    }

    @Test
    void checkFeatureToggle_WhenFeatureDisabled_ShouldThrowException() throws Throwable {

        Method method = TestService.class.getMethod("testMethod");
        when(joinPoint.getSignature()).thenReturn(new TestMethodSignature(method));
        when(featureToggleService.isFeatureEnabled(FeatureToggles.COSMO_CATS)).thenReturn(false);

        assertThrows(FeatureNotAvailableException.class, 
            () -> aspect.checkFeatureToggle(joinPoint));
        
        verify(featureToggleService).isFeatureEnabled(FeatureToggles.COSMO_CATS);
        verify(joinPoint, never()).proceed();
    }

    private static class TestService {
        @FeatureToggle(feature = FeatureToggles.COSMO_CATS)
        public String testMethod() {
            return "test";
        }
    }

    private static class TestMethodSignature implements org.aspectj.lang.reflect.MethodSignature {
        private final Method method;

        TestMethodSignature(Method method) {
            this.method = method;
        }

        @Override
        public Method getMethod() {
            return method;
        }

        @Override
        public Class<?> getReturnType() { return null; }
        
        @Override
        public Class<?>[] getParameterTypes() { return new Class<?>[0]; }
        
        @Override
        public String[] getParameterNames() { return new String[0]; }
        
        @Override
        public Class<?>[] getExceptionTypes() { return new Class<?>[0]; }
        
        @Override
        public String toShortString() { return ""; }
        
        @Override
        public String toLongString() { return ""; }
        
        @Override
        public String getName() { return ""; }
        
        @Override
        public int getModifiers() { return 0; }
        
        @Override
        public Class<?> getDeclaringType() { return null; }
        
        @Override
        public String getDeclaringTypeName() { return ""; }
    }
}
