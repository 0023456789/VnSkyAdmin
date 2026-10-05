package org.example.adminsky.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.example.adminsky.dto.request.AppQuotaRequest;
import org.example.adminsky.dto.request.BonusRequest;
import org.example.adminsky.dto.request.PlanCreationRequest;
import org.example.adminsky.dto.request.PlanUpdateRequest;
import org.example.adminsky.dto.response.PageResponse;
import org.example.adminsky.dto.response.PlanResponse;
import org.example.adminsky.dto.response.PlanSummaryResponse;
import org.example.adminsky.entity.App;
import org.example.adminsky.entity.Plan;
import org.example.adminsky.entity.PlanAppQuota;
import org.example.adminsky.entity.PlanFirstCycleBonus;
import org.example.adminsky.enums.BonusType;
import org.example.adminsky.exception.AppException;
import org.example.adminsky.exception.ErrorCode;
import org.example.adminsky.mapper.PlanMapper;
import org.example.adminsky.repository.AppRepository;
import org.example.adminsky.repository.PlanRepository;
import org.example.adminsky.repository.specification.PlanSpecifications;
import org.example.adminsky.constant.SortFields;
import org.example.adminsky.util.PageRequestFactory;
import org.example.adminsky.validator.PlanRequestValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlanService {
    PlanRepository planRepository;
    AppRepository appRepository;
    PlanMapper planMapper;
    PlanRequestValidator planRequestValidator;
    Clock clock;

    /** Creates a plan and persists its bonus and app-quota children atomically. */
    @Transactional
    public PlanResponse createPlan(PlanCreationRequest request) {
        planRequestValidator.validate(request, null);
        Plan plan = planMapper.toPlan(request);
        syncBonuses(plan, request.getFirstCycleBonuses());
        syncAppQuotas(plan, request.getAppQuotas());
        return planMapper.toPlanResponse(planRepository.save(plan));
    }

    /** Returns a SQL-paginated list with grouped child counts for this page only. */
    @Transactional(readOnly = true)
    public PageResponse<PlanSummaryResponse> getPlans(Boolean isActive, String keyword, Integer durationMonths,
                                                       int page, int size, String sort) {
        Pageable pageable = PageRequestFactory.of(page, size, sort, SortFields.PLANS);
        Page<Plan> plans = planRepository.findAll(
                PlanSpecifications.hasActive(isActive)
                        .and(PlanSpecifications.keywordLike(keyword))
                        .and(PlanSpecifications.hasDuration(durationMonths)), pageable);
        return toPageResponse(plans);
    }

    /** Loads plan details and its child collections through two entity-graph queries. */
    @Transactional(readOnly = true)
    public PlanResponse getPlan(Long planId) {
        return planMapper.toPlanResponse(findPlanDetail(planId));
    }

    /** Replaces all plan fields and child rows while holding a write lock. */
    @Transactional
    public PlanResponse updatePlan(Long planId, PlanUpdateRequest request) {
        Plan plan = planRepository.findForUpdateById(planId).orElseThrow(() -> planNotFound(planId));
        planRequestValidator.validate(request, planId);
        planMapper.updatePlan(plan, request);
        syncBonuses(plan, request.getFirstCycleBonuses());
        syncAppQuotas(plan, request.getAppQuotas());
        plan.setUpdatedAt(Instant.now(clock));
        return planMapper.toPlanResponse(planRepository.saveAndFlush(plan));
    }

    /** Updates status under a write lock and returns the refreshed plan. */
    @Transactional
    public PlanResponse updatePlanStatus(Long planId, Boolean isActive) {
        Plan plan = planRepository.findForUpdateById(planId).orElseThrow(() -> planNotFound(planId));
        plan.setActive(isActive);
        return planMapper.toPlanResponse(findPlanDetail(planId));
    }

    /** Hard deletes a plan; RESTRICT foreign keys are reported as PLAN_IN_USE. */
    @Transactional
    public String deletePlan(Long planId) {
        Plan plan = planRepository.findForUpdateById(planId).orElseThrow(() -> planNotFound(planId));
        planRepository.delete(plan);
        planRepository.flush();
        return "Plan has been deleted";
    }

    private void syncBonuses(Plan plan, List<BonusRequest> requests) {
        Set<BonusType> bonusTypes = new HashSet<>();
        Map<BonusType, PlanFirstCycleBonus> existing = plan.getBonuses().stream()
                .collect(Collectors.toMap(PlanFirstCycleBonus::getBonusType, bonus -> bonus));
        for (BonusRequest request : requests) {
            if (!bonusTypes.add(request.getBonusType())) throw new AppException(ErrorCode.PLAN_BONUS_DUPLICATED);
            PlanFirstCycleBonus bonus = existing.remove(request.getBonusType());
            if (bonus == null) {
                plan.addBonus(planMapper.toEntity(request));
            } else {
                bonus.setAmount(request.getAmount());
            }
        }
        existing.values().forEach(plan::removeBonus);
    }

    private void syncAppQuotas(Plan plan, List<AppQuotaRequest> requests) {
        Set<Long> appIds = new HashSet<>();
        Map<Long, PlanAppQuota> existing = plan.getAppQuotas().stream()
                .collect(Collectors.toMap(quota -> quota.getApp().getId(), quota -> quota));
        Map<Long, App> appById = new HashMap<>();
        for (App app : appRepository.findAllById(requests.stream().map(AppQuotaRequest::getAppId).distinct().toList())) {
            appById.put(app.getId(), app);
        }
        for (AppQuotaRequest request : requests) {
            if (!appIds.add(request.getAppId())) throw new AppException(ErrorCode.PLAN_APP_QUOTA_DUPLICATED);
            App app = appById.get(request.getAppId());
            if (app == null) throw new AppException(ErrorCode.APP_NOT_FOUND);
            if (!app.isActive()) throw new AppException(ErrorCode.APP_INACTIVE);
            PlanAppQuota quota = existing.remove(request.getAppId());
            if (quota == null) {
                quota = planMapper.toEntity(request);
                quota.setApp(app);
                plan.addAppQuota(quota);
            } else {
                quota.setQuotaType(request.getQuotaType());
                quota.setQuotaMb(request.getQuotaMb());
            }
        }
        existing.values().forEach(plan::removeAppQuota);
    }

    private PageResponse<PlanSummaryResponse> toPageResponse(Page<Plan> page) {
        if (page.isEmpty()) {
            return PageResponse.<PlanSummaryResponse>builder()
                    .content(List.of())
                    .page(page.getNumber())
                    .size(page.getSize())
                    .totalElements(page.getTotalElements())
                    .totalPages(page.getTotalPages())
                    .build();
        }
        return PageResponse.from(page.map(planMapper::toPlanSummary));
    }

    private Plan findPlanDetail(Long planId) {
        Plan plan = planRepository.findWithBonusesById(planId).orElseThrow(() -> planNotFound(planId));
        return planRepository.findWithAppQuotasById(planId).orElse(plan);
    }

    private AppException planNotFound(Long planId) {
        return new AppException(ErrorCode.PLAN_NOT_FOUND, "Plan " + planId + " not found");
    }

}
