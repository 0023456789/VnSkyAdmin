package org.example.adminsky.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class AllowedValuesValidator implements ConstraintValidator<AllowedValuesConstraint, Integer> {
    private int[] values;
    @Override public void initialize(AllowedValuesConstraint constraint) { values = constraint.values(); }
    @Override public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value == null || Arrays.stream(values).anyMatch(allowed -> allowed == value);
    }
}
