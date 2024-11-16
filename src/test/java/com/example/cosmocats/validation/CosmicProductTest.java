package com.example.cosmocats.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.example.cosmocats.validation.interfaces.CosmicProduct;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;

public class CosmicProductTest {
    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public static class TestEntity {
        
        @NotBlank
        @CosmicProduct
        private String product;

        public TestEntity(String product) {
            this.product = product;
        }
    }

    @Test
    public void whenContainsWord_thenValid() {
        TestEntity entity = new TestEntity("space");
        var violations = validator.validate(entity);
        assertEquals(0, violations.size(), "Expected no violations when string is word");
    }

    @Test
    public void whenWordPresent_thenValid() {
        TestEntity entity = new TestEntity("space yarn or idk");
        var violations = validator.validate(entity);
        assertEquals(0, violations.size(), "Expected no violations when word is present in string");
    }

    @Test
    public void whenContainsWordIgnoreCase_thenValid() {
        TestEntity entity = new TestEntity("Space");
        var violations = validator.validate(entity);
        assertEquals(0, violations.size(), "Expected no violations when ignoring case");
    }

    @Test
    public void whenDoesNotContainWord_thenInvalid() {
        TestEntity entity = new TestEntity("just normal yarn");
        var violations = validator.validate(entity);
        assertNotEquals(0, violations.size(), "Expected violation when word is not in the list");
    }


}
