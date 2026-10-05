package org.example.adminsky.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.adminsky.dto.request.PromoDiscountFields;
import org.example.adminsky.enums.DiscountType;

import java.math.BigDecimal;

public class PromoDiscountValidator implements ConstraintValidator<PromoDiscountConstraint, PromoDiscountFields> {
    @Override
    public boolean isValid(PromoDiscountFields request, ConstraintValidatorContext context) {
        if (request == null) return true;

        if (request.getDiscountType() != null && request.getDiscountValue() != null) {
            if (request.getDiscountType() == DiscountType.PERCENT
                    && request.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
                return violation(context, "discountValue", context.getDefaultConstraintMessageTemplate());
            }
            if (request.getDiscountType() == DiscountType.FIXED_AMOUNT
                    && !isWholeVnd(request.getDiscountValue())) {
                return violation(context, "discountValue", context.getDefaultConstraintMessageTemplate());
            }
        }

        if (request.getMaxDiscountAmount() != null && request.getDiscountType() != DiscountType.PERCENT) {
            return violation(context, "maxDiscountAmount", context.getDefaultConstraintMessageTemplate());
        }

        if (request.getValidFrom() != null && request.getValidTo() != null
                && !request.getValidTo().isAfter(request.getValidFrom())) {
            return violation(context, "validTo", "PROMO_DATE_RANGE_INVALID");
        }
        return true;
    }

    private boolean isWholeVnd(BigDecimal amount) {
        return amount.stripTrailingZeros().scale() <= 0;
    }

    private boolean violation(ConstraintValidatorContext context, String field, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .addConstraintViolation();
        return false;
    }
}
