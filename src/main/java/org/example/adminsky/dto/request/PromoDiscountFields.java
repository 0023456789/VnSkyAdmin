package org.example.adminsky.dto.request;

import java.math.BigDecimal;
import java.time.Instant;
import org.example.adminsky.enums.DiscountType;

public interface PromoDiscountFields {
    DiscountType getDiscountType();
    BigDecimal getDiscountValue();
    Long getMaxDiscountAmount();
    Instant getValidFrom();
    Instant getValidTo();
}
