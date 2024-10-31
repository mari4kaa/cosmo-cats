package com.example.cosmocats.validation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.example.cosmocats.validation.interfaces.ContainsWord;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ContainsWordValidatorTest {

    private static Validator validator;

    @BeforeAll
    public static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public static class TestEntity {
        
        @ContainsWord(value = {"admin", "user"}, ignoreCase = true)
        private String role;

        public TestEntity(String role) {
            this.role = role;
        }
    }

    public static class SensitiveEntity {
        
        @ContainsWord(value = {"admin", "user"}, ignoreCase = false)
        private String role;

        public SensitiveEntity(String role) {
            this.role = role;
        }
    }

    @Test
    public void whenContainsWordIgnoreCase_thenValid() {
        TestEntity entity = new TestEntity("Admin");
        var violations = validator.validate(entity);
        assertEquals(0, violations.size(), "Expected no violations when ignoring case");
    }

    @Test
    public void whenContainsWordAmongOthers_thenValid() {
        TestEntity entity = new TestEntity("this guy is admin or something?");
        var violations = validator.validate(entity);
        assertEquals(0, violations.size(), "Expected no violations when word is present");
    }

    @Test
    public void whenDoesNotContainWordIgnoreCase_thenInvalid() {
        TestEntity entity = new TestEntity("viewer");
        var violations = validator.validate(entity);
        assertNotEquals(0, violations.size(), "Expected violation when word is not in the list");
    }

    @Test
    public void whenContainsWordSensitiveCase_thenValid() {
        SensitiveEntity entity = new SensitiveEntity("admin");
        var violations = validator.validate(entity);
        assertEquals(0, violations.size(), "Expected no violations with case sensitivity");
    }

    @Test
    public void whenDoesNotContainExactWordSensitiveCase_thenInvalid() {
        SensitiveEntity entity = new SensitiveEntity("Admin");
        var violations = validator.validate(entity);
        assertNotEquals(0, violations.size(), "Expected violation when case-sensitive match fails");
    }
}
