package org.example.adminsky.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.adminsky.dto.request.PlanQuotaFields;
import org.example.adminsky.enums.QuotaType;

public class PlanQuotaValidator implements ConstraintValidator<PlanQuotaConstraint, PlanQuotaFields> {

    @Override
    public boolean isValid(PlanQuotaFields value, ConstraintValidatorContext context) {
        if (value == null || value.getQuotaType() == null) {
            return true;
        }
        boolean ok;
        if (value.getQuotaType() == QuotaType.PER_CYCLE) {
            ok = value.getCycleDays() != null
                    && value.getDurationMonths() != null
                    && value.getCycleDays() <= value.getDurationMonths() * 30;
        } else {
            ok = value.getCycleDays() == null;
        }
        if (!ok) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("PLAN_QUOTA_INVALID")
                    .addPropertyNode("cycleDays")
                    .addConstraintViolation();
        }
        return ok;
    }
}
