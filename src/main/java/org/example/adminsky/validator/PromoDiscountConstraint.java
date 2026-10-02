package org.example.adminsky.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = PromoDiscountValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface PromoDiscountConstraint {
    String message() default "PROMO_DISCOUNT_INVALID";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
