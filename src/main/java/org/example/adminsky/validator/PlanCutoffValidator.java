package org.example.adminsky.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.adminsky.dto.request.PlanRequest;
import org.example.adminsky.enums.CutoffPolicy;

public class PlanCutoffValidator implements ConstraintValidator<PlanCutoffConstraint, PlanRequest> {
    @Override public boolean isValid(PlanRequest request, ConstraintValidatorContext context) {
        if (request == null || request.getCutoffPolicy() == null) return true;
        boolean valid = request.getCutoffPolicy() == CutoffPolicy.THROTTLE
                ? request.getThrottleSpeedKbps() != null
                : request.getThrottleSpeedKbps() == null;
        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("throttleSpeedKbps").addConstraintViolation();
        }
        return valid;
    }
}
