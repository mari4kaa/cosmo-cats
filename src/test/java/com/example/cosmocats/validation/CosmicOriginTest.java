package com.example.cosmocats.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.example.cosmocats.validation.interfaces.CosmicOrigin;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;

public class CosmicOriginTest {
    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public static class TestEntity {
        
        @NotBlank
        @CosmicOrigin
        private String origin;

        public TestEntity(String origin) {
            this.origin = origin;
        }
    }

    @Test
    public void whenContainsWord_thenValid() {
        TestEntity entity = new TestEntity("Earth");
        var violations = validator.validate(entity);
        assertEquals(0, violations.size(), "Expected no violations when string is word");
    }

    @Test
    public void whenDoesNotContainWord_thenInvalid() {
        TestEntity entity = new TestEntity("AnyPlanet");
        var violations = validator.validate(entity);
        assertNotEquals(0, violations.size(), "Expected violation when word is not in the list");
    }

    @Test
    public void whenNotEqual_thenInvalid() {
        TestEntity entity = new TestEntity("Earth, Ukraine");
        var violations = validator.validate(entity);
        assertNotEquals(0, violations.size(), "Expected violation when not only word is present in string");
    }

    @Test
    public void whenContainsWordIgnoreCase_thenInvalid() {
        TestEntity entity = new TestEntity("earth");
        var violations = validator.validate(entity);
        assertNotEquals(0, violations.size(), "Expected violation when ignoring case");
    }


}
