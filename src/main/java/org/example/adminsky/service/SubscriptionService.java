package org.example.adminsky.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import org.example.adminsky.constant.SortFields;
import org.example.adminsky.dto.request.SubscriptionCreationRequest;
import org.example.adminsky.dto.response.PageResponse;
import org.example.adminsky.dto.response.SubscriptionResponse;
import org.example.adminsky.entity.Plan;
import org.example.adminsky.entity.PlanSubscription;
import org.example.adminsky.entity.PlanSubscriptionBonus;
import org.example.adminsky.entity.PromoCode;
import org.example.adminsky.enums.PromoReasonCode;
import org.example.adminsky.enums.SubscriptionStatus;
import org.example.adminsky.exception.AppException;
import org.example.adminsky.exception.ErrorCode;
import org.example.adminsky.mapper.SubscriptionMapper;
import org.example.adminsky.repository.PlanRepository;
import org.example.adminsky.repository.PlanSubscriptionRepository;
import org.example.adminsky.repository.PromoCodeRepository;
import org.example.adminsky.repository.specification.SubscriptionSpecifications;
import org.example.adminsky.util.CodeNormalizer;
import org.example.adminsky.util.PageRequestFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubscriptionService {
    private final PlanSubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final PromoCodeRepository promoCodeRepository;
    private final PromoEligibilityChecker eligibilityChecker;
    private final PromoDiscountCalculator discountCalculator;
    private final FirstCycleBonusResolver firstCycleBonusResolver;
    private final PlanExpiryCalculator expiryCalculator;
    private final SubscriptionMapper subscriptionMapper;
    private final Clock clock;

    public SubscriptionService(
            PlanSubscriptionRepository subscriptionRepository,
            PlanRepository planRepository,
            PromoCodeRepository promoCodeRepository,
            PromoEligibilityChecker eligibilityChecker,
            PromoDiscountCalculator discountCalculator,
            FirstCycleBonusResolver firstCycleBonusResolver,
            PlanExpiryCalculator expiryCalculator,
            SubscriptionMapper subscriptionMapper,
            Clock clock) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
        this.promoCodeRepository = promoCodeRepository;
        this.eligibilityChecker = eligibilityChecker;
        this.discountCalculator = discountCalculator;
        this.firstCycleBonusResolver = firstCycleBonusResolver;
        this.expiryCalculator = expiryCalculator;
        this.subscriptionMapper = subscriptionMapper;
        this.clock = clock;
    }

    @Transactional
    public SubscriptionResponse createSubscription(String idempotencyKey, SubscriptionCreationRequest request) {
        if (idempotencyKey == null || idempotencyKey.isBlank()
                || idempotencyKey.length() < 8 || idempotencyKey.length() > 64) {
            throw new AppException(ErrorCode.IDEMPOTENCY_KEY_INVALID);
        }
        String promoCodeValue = CodeNormalizer.normalize(request.getPromoCode());
        if (promoCodeValue != null && promoCodeValue.isEmpty()) promoCodeValue = null;
        String msisdn = request.getMsisdn();

        subscriptionRepository.lockMsisdn(msisdn);
        var replay = subscriptionRepository.findByMsisdnAndIdempotencyKey(msisdn, idempotencyKey);
        if (replay.isPresent()) {
            return replayResponse(replay.get(), request.getPlanId(), promoCodeValue);
        }

        Plan plan = planRepository.findForShareById(request.getPlanId())
                .orElseThrow(() -> new AppException(ErrorCode.PLAN_NOT_FOUND));
        if (!plan.isActive()) throw new AppException(ErrorCode.PLAN_INACTIVE);

        Instant now = Instant.now(clock);
        BigDecimal discount = BigDecimal.ZERO;
        PromoCode promo = null;
        if (promoCodeValue != null) {
            promo = promoCodeRepository.findForUpdateByCode(promoCodeValue)
                    .orElseThrow(() -> promoNotAvailable(PromoReasonCode.NOT_FOUND));
            var reason = eligibilityChecker.check(promo, plan, plan.getPrice(), now, msisdn);
            if (reason.isPresent()) throw promoNotAvailable(reason.get());
            discount = discountCalculator.calculate(promo, plan.getPrice());
            if (promoCodeRepository.consume(promo.getId(), now) == 0) {
                throw promoNotAvailable(PromoReasonCode.EXHAUSTED);
            }
        }

        boolean firstCycle = firstCycleBonusResolver.isNewSubscriber(msisdn);
        PlanSubscription subscription = PlanSubscription.builder()
                .plan(plan)
                .msisdn(msisdn)
                .promoCode(promo)
                .originalPrice(plan.getPrice())
                .discountAmount(discount)
                .finalPrice(plan.getPrice().subtract(discount))
                .firstCycle(firstCycle)
                .promoSingleUse(promo != null && Integer.valueOf(1).equals(promo.getMaxUsesPerMsisdn()))
                .idempotencyKey(idempotencyKey)
                .status(SubscriptionStatus.ACTIVE)
                .activatedAt(now)
                .expiresAt(expiryCalculator.expiresAt(now, plan.getDurationMonths()))
                .build();

        if (firstCycle) {
            plan.getBonuses().forEach(bonus -> subscription.addBonus(PlanSubscriptionBonus.builder()
                    .bonusType(bonus.getBonusType())
                    .amount(bonus.getAmount())
                    .build()));
        }
        return subscriptionMapper.toSubscriptionResponse(subscriptionRepository.saveAndFlush(subscription));
    }

    @Transactional(readOnly = true)
    public PageResponse<SubscriptionResponse> getSubscriptions(
            String msisdn, SubscriptionStatus status, Integer page, Integer size, String sort) {
        Pageable pageable = PageRequestFactory.of(
                page == null ? 0 : page,
                size == null ? 20 : size,
                sort == null ? SortFields.DEFAULT_SORT : sort,
                SortFields.SUBSCRIPTION);
        Page<PlanSubscription> result = subscriptionRepository.findAll(
                SubscriptionSpecifications.filter(msisdn, status), pageable);
        return PageResponse.from(result.map(subscriptionMapper::toSubscriptionResponse));
    }

    @Transactional(readOnly = true)
    public SubscriptionResponse getSubscription(Long id) {
        PlanSubscription subscription = subscriptionRepository.findWithDetailById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUBSCRIPTION_NOT_FOUND));
        return subscriptionMapper.toSubscriptionResponse(subscription);
    }

    @Transactional
    public int expireDueSubscriptions() {
        return subscriptionRepository.expireActiveSubscriptions(
                Instant.now(clock), SubscriptionStatus.ACTIVE, SubscriptionStatus.EXPIRED);
    }

    private SubscriptionResponse replayResponse(PlanSubscription existing, Long planId, String promoCode) {
        String existingPromo = existing.getPromoCode() == null ? null : existing.getPromoCode().getCode();
        if (!Objects.equals(existing.getPlan().getId(), planId)
                || !Objects.equals(existingPromo, promoCode)) {
            throw new AppException(ErrorCode.IDEMPOTENCY_KEY_REUSED);
        }
        return subscriptionMapper.toSubscriptionResponse(existing);
    }

    private AppException promoNotAvailable(PromoReasonCode reason) {
        return new AppException(ErrorCode.PROMO_NOT_AVAILABLE, Map.of("reasonCode", reason));
    }
}
