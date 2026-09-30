package org.example.adminsky.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AllowedValuesValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedValuesConstraint {
    String message() default "INVALID_KEY";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int[] values();
}
