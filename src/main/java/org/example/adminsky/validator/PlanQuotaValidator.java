package org.example.adminsky.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.adminsky.dto.request.PlanRequest;
import org.example.adminsky.enums.QuotaType;

public class PlanQuotaValidator implements ConstraintValidator<PlanQuotaConstraint, PlanRequest> {
    @Override public boolean isValid(PlanRequest request, ConstraintValidatorContext context) {
        if (request == null || request.getQuotaType() == null) return true;
        boolean valid = request.getQuotaType() == QuotaType.PER_CYCLE
                ? request.getCycleDays() != null && request.getDurationMonths() != null
                    && request.getCycleDays() <= request.getDurationMonths() * 30
                : request.getCycleDays() == null;
        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("cycleDays").addConstraintViolation();
        }
        return valid;
    }
}
