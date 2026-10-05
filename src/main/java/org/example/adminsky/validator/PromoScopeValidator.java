package org.example.adminsky.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.adminsky.dto.request.PromoScopeFields;

public class PromoScopeValidator implements ConstraintValidator<PromoScopeConstraint, PromoScopeFields> {
    @Override
    public boolean isValid(PromoScopeFields request, ConstraintValidatorContext context) {
        if (request == null || request.getAppliesToAllPlans() == null) return true;
        if (request.getAppliesToAllPlans()) return true;
        boolean valid = request.getPlanIds() != null && !request.getPlanIds().isEmpty();
        if (valid) return true;

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("planIds")
                .addConstraintViolation();
        return false;
    }
}
