package org.example.adminsky.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;
import java.lang.annotation.Inherited;

@Documented
@Constraint(validatedBy = PlanQuotaValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface PlanQuotaConstraint {
    String message() default "PLAN_QUOTA_INVALID";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
