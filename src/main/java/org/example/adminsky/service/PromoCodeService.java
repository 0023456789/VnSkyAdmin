package org.example.adminsky.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.adminsky.constant.SortFields;
import org.example.adminsky.dto.request.PromoCreationRequest;
import org.example.adminsky.dto.request.PromoUpdateRequest;
import org.example.adminsky.dto.request.PromoValidateRequest;
import org.example.adminsky.dto.response.PageResponse;
import org.example.adminsky.dto.response.PromoResponse;
import org.example.adminsky.dto.response.PromoValidateResponse;
import org.example.adminsky.entity.Plan;
import org.example.adminsky.entity.PromoCode;
import org.example.adminsky.enums.PromoReasonCode;
import org.example.adminsky.enums.PromoWarningCode;
import org.example.adminsky.exception.AppException;
import org.example.adminsky.exception.ErrorCode;
import org.example.adminsky.mapper.PromoMapper;
import org.example.adminsky.repository.PlanRepository;
import org.example.adminsky.repository.PlanSubscriptionRepository;
import org.example.adminsky.repository.PromoCodeRepository;
import org.example.adminsky.repository.specification.PromoSpecifications;
import org.example.adminsky.util.CodeNormalizer;
import org.example.adminsky.util.PageRequestFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PromoCodeService {
    PromoCodeRepository promoCodeRepository;
    PlanRepository planRepository;
    PlanSubscriptionRepository planSubscriptionRepository;
    PromoMapper promoMapper;
    PromoEligibilityChecker eligibilityChecker;
    PromoDiscountCalculator discountCalculator;
    Clock clock;

    @Transactional
    public PromoResponse createPromo(PromoCreationRequest request) {
        String code = CodeNormalizer.normalize(request.getCode());
        if (promoCodeRepository.existsByCode(code)) throw new AppException(ErrorCode.PROMO_CODE_EXISTED);
        PromoCode promo = promoMapper.toPromo(request);
        promo.setCode(code);
        applyScope(promo, request.getAppliesToAllPlans(), request.getPlanIds());
        return promoMapper.toPromoResponse(promoCodeRepository.saveAndFlush(promo));
    }

    @Transactional(readOnly = true)
    public PageResponse<PromoResponse> getPromos(Boolean active, String keyword, Integer page, Integer size, String sort) {
        Pageable pageable = PageRequestFactory.of(page == null ? 0 : page, size == null ? 20 : size,
                sort == null ? SortFields.DEFAULT_SORT : sort, SortFields.PROMO);
        return PageResponse.from(promoCodeRepository.findAll(PromoSpecifications.filter(active, keyword), pageable)
                .map(promoMapper::toPromoListItem));
    }

    @Transactional(readOnly = true)
    public PromoResponse getPromo(Long id) {
        return promoMapper.toPromoResponse(promoCodeRepository.findWithPlansById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROMO_NOT_FOUND)));
    }

    @Transactional
    public PromoResponse updatePromo(Long id, PromoUpdateRequest request) {
        PromoCode promo = promoCodeRepository.findForUpdateById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROMO_NOT_FOUND));
        String code = CodeNormalizer.normalize(request.getCode());
        if (promoCodeRepository.existsByCodeAndIdNot(code, id)) throw new AppException(ErrorCode.PROMO_CODE_EXISTED);
        if (request.getUsageLimit() != null && request.getUsageLimit() < promo.getUsedCount())
            throw new AppException(ErrorCode.PROMO_LIMIT_BELOW_USED);
        if (request.getMaxUsesPerMsisdn() != null) {
            List<Long> highestUsage = planSubscriptionRepository.findUsesPerMsisdnDesc(
                    id, PageRequest.of(0, 1));
            if (!highestUsage.isEmpty() && request.getMaxUsesPerMsisdn() < highestUsage.get(0))
                throw new AppException(ErrorCode.PROMO_LIMIT_BELOW_USAGE);
        }
        promoMapper.updatePromo(promo, request);
        promo.setCode(code);
        applyScope(promo, request.getAppliesToAllPlans(), request.getPlanIds());
        promoCodeRepository.flush();
        return promoMapper.toPromoResponse(promo);
    }

    @Transactional
    public PromoResponse updatePromoStatus(Long id, boolean active) {
        PromoCode promo = promoCodeRepository.findForUpdateById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROMO_NOT_FOUND));
        promo.setActive(active);
        return promoMapper.toPromoResponse(promo);
    }

    @Transactional
    public String deletePromo(Long id) {
        PromoCode promo = promoCodeRepository.findForUpdateById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROMO_NOT_FOUND));
        promoCodeRepository.delete(promo);
        promoCodeRepository.flush();
        return "Promo code has been deleted";
    }

    @Transactional(readOnly = true)
    public PromoValidateResponse validatePromo(PromoValidateRequest request) {
        Plan plan = planRepository.findById(request.getPlanId()).orElseThrow(() -> new AppException(ErrorCode.PLAN_NOT_FOUND));
        BigDecimal price = plan.getPrice();
        List<PromoWarningCode> warnings = plan.isActive() ? List.of() : List.of(PromoWarningCode.PLAN_INACTIVE);
        PromoCode promo = promoCodeRepository.findByCode(CodeNormalizer.normalize(request.getCode())).orElse(null);
        if (promo == null) return invalid(PromoReasonCode.NOT_FOUND, price, warnings);
        Optional<PromoReasonCode> reason = eligibilityChecker.check(
                promo, plan, price, Instant.now(clock), request.getMsisdn());
        if (reason.isPresent()) return invalid(reason.get(), price, warnings);
        BigDecimal discount = discountCalculator.calculate(promo, price);
        return PromoValidateResponse.builder().valid(true).discountAmount(discount.longValueExact())
                .finalPrice(price.subtract(discount).longValueExact()).warnings(warnings).build();
    }

    private PromoValidateResponse invalid(
            PromoReasonCode reason, BigDecimal price, List<PromoWarningCode> warnings) {
        return PromoValidateResponse.builder().valid(false).reasonCode(reason).discountAmount(0L)
                .finalPrice(price.longValueExact()).warnings(warnings).build();
    }

    private void applyScope(PromoCode promo, Boolean appliesToAllPlans, List<Long> planIds) {
        promo.getPlans().clear();
        promo.setAppliesToAllPlans(Boolean.TRUE.equals(appliesToAllPlans));
        if (promo.isAppliesToAllPlans()) return;
        Set<Long> ids = new LinkedHashSet<>(planIds == null ? List.of() : planIds);
        List<Plan> plans = planRepository.findAllById(ids);
        if (plans.size() != ids.size()) throw new AppException(ErrorCode.PROMO_SCOPE_INVALID);
        promo.getPlans().addAll(plans);
    }
}
