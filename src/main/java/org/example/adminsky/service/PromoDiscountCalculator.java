package org.example.adminsky.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.example.adminsky.entity.PromoCode;
import org.example.adminsky.enums.DiscountType;
import org.springframework.stereotype.Component;

@Component
public class PromoDiscountCalculator {
    public BigDecimal calculate(PromoCode promo, BigDecimal price) {
        BigDecimal discount = promo.getDiscountType() == DiscountType.FIXED_AMOUNT
                ? promo.getDiscountValue()
                : price.multiply(promo.getDiscountValue()).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
        if (promo.getMaxDiscountAmount() != null) discount = discount.min(promo.getMaxDiscountAmount());
        return discount.min(price).max(BigDecimal.ZERO).setScale(0, RoundingMode.DOWN);
    }
}
