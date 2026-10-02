package org.example.adminsky.service;

import lombok.RequiredArgsConstructor;
import org.example.adminsky.entity.Plan;
import org.example.adminsky.entity.PromoCode;
import org.example.adminsky.enums.PromoReasonCode;
import org.example.adminsky.enums.SubscriptionStatus;
import org.example.adminsky.repository.PlanSubscriptionRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PromoEligibilityChecker {
    private final PlanSubscriptionRepository planSubscriptionRepository;

    public Optional<PromoReasonCode> check(
            PromoCode promo, Plan plan, BigDecimal orderAmount, Instant now, String msisdn) {
        if (!promo.isActive()) return Optional.of(PromoReasonCode.INACTIVE);
        if (promo.getValidFrom() != null && now.isBefore(promo.getValidFrom()))
            return Optional.of(PromoReasonCode.NOT_STARTED);
        if (promo.getValidTo() != null && now.isAfter(promo.getValidTo())) return Optional.of(PromoReasonCode.EXPIRED);
        if (promo.getUsageLimit() != null && promo.getUsedCount() >= promo.getUsageLimit())
            return Optional.of(PromoReasonCode.EXHAUSTED);
        if (msisdn != null && promo.getMaxUsesPerMsisdn() != null
                && planSubscriptionRepository.countByPromoCodeIdAndMsisdnAndStatusNot(
                                promo.getId(), msisdn, SubscriptionStatus.CANCELLED)
                        >= promo.getMaxUsesPerMsisdn())
            return Optional.of(PromoReasonCode.USER_LIMIT_REACHED);
        if (!promo.isAppliesToAllPlans() && promo.getPlans().stream().noneMatch(p -> p.getId().equals(plan.getId())))
            return Optional.of(PromoReasonCode.NOT_APPLICABLE);
        if (orderAmount != null && orderAmount.compareTo(promo.getMinOrderAmount()) < 0)
            return Optional.of(PromoReasonCode.MIN_ORDER_NOT_MET);
        return Optional.empty();
    }
}
