package org.example.adminsky.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PlanCutoffValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface PlanCutoffConstraint {
    String message() default "PLAN_CUTOFF_INVALID";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
