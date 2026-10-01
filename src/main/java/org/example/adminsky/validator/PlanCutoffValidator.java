package org.example.adminsky.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.adminsky.dto.request.PlanCutoffFields;
import org.example.adminsky.enums.CutoffPolicy;

public class PlanCutoffValidator implements ConstraintValidator<PlanCutoffConstraint, PlanCutoffFields> {

    @Override
    public boolean isValid(PlanCutoffFields value, ConstraintValidatorContext context) {
        if (value == null || value.getCutoffPolicy() == null) {
            return true;
        }
        boolean ok = value.getCutoffPolicy() == CutoffPolicy.THROTTLE
                ? value.getThrottleSpeedKbps() != null
                : value.getThrottleSpeedKbps() == null;
        if (!ok) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("PLAN_CUTOFF_INVALID")
                    .addPropertyNode("throttleSpeedKbps")
                    .addConstraintViolation();
        }
        return ok;
    }
}
