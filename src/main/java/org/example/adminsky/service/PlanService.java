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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.SQLException;
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

    /** Creates a plan and persists its bonus and app-quota children atomically. */
    @Transactional
    public PlanResponse createPlan(PlanCreationRequest request) {
        planRequestValidator.validate(request, null);
        Plan plan = planMapper.toPlan(request);
        syncBonuses(plan, request.getBonuses());
        syncAppQuotas(plan, request.getAppQuotas());
        try { return planMapper.toPlanResponse(planRepository.save(plan)); }
        catch (DataIntegrityViolationException exception) { throw new AppException(ErrorCode.PLAN_CODE_EXISTED); }
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
        Plan plan = planRepository.findByIdForUpdate(planId).orElseThrow(() -> planNotFound(planId));
        planRequestValidator.validate(request, planId);
        planMapper.updatePlan(plan, request);
        syncBonuses(plan, request.getBonuses());
        syncAppQuotas(plan, request.getAppQuotas());
        return planMapper.toPlanResponse(planRepository.saveAndFlush(plan));
    }

    /** Updates status under a write lock and returns the refreshed plan. */
    @Transactional
    public PlanResponse updatePlanStatus(Long planId, Boolean isActive) {
        Plan plan = planRepository.findByIdForUpdate(planId).orElseThrow(() -> planNotFound(planId));
        if (planRepository.updateStatus(planId, isActive) == 0) throw planNotFound(planId);
        return planMapper.toPlanResponse(findPlanDetail(planId));
    }

    /** Hard deletes a plan; RESTRICT foreign keys are reported as PLAN_IN_USE. */
    @Transactional
    public String deletePlan(Long planId) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> planNotFound(planId));
        if (planRepository.subscriptionTableExists() && planRepository.existsSubscription(planId)) {
            throw new AppException(ErrorCode.PLAN_IN_USE);
        }
        try {
            planRepository.delete(plan);
            planRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            if (isForeignKeyViolation(exception)) throw new AppException(ErrorCode.PLAN_IN_USE);
            throw exception;
        }
        return "Plan has been deleted";
    }

    private void syncBonuses(Plan plan, List<BonusRequest> requests) {
        Set<BonusType> bonusTypes = new HashSet<>();
        Set<PlanFirstCycleBonus> bonuses = new HashSet<>();
        for (BonusRequest request : requests) {
            if (!bonusTypes.add(request.getBonusType())) throw new AppException(ErrorCode.PLAN_BONUS_DUPLICATED);
            PlanFirstCycleBonus bonus = planMapper.toEntity(request);
            bonus.setPlan(plan);
            bonuses.add(bonus);
        }
        plan.getBonuses().clear();
        plan.getBonuses().addAll(bonuses);
    }

    private void syncAppQuotas(Plan plan, List<AppQuotaRequest> requests) {
        Set<Long> appIds = new HashSet<>();
        Set<PlanAppQuota> quotas = new HashSet<>();
        Map<Long, App> appById = new HashMap<>();
        for (App app : appRepository.findAllById(requests.stream().map(AppQuotaRequest::getAppId).distinct().toList())) {
            appById.put(app.getId(), app);
        }
        for (AppQuotaRequest request : requests) {
            if (!appIds.add(request.getAppId())) throw new AppException(ErrorCode.PLAN_APP_QUOTA_DUPLICATED);
            App app = appById.get(request.getAppId());
            if (app == null) throw new AppException(ErrorCode.APP_NOT_FOUND);
            if (!app.isActive()) throw new AppException(ErrorCode.APP_INACTIVE);
            PlanAppQuota quota = planMapper.toEntity(request);
            quota.setPlan(plan);
            quota.setApp(app);
            quotas.add(quota);
        }
        plan.getAppQuotas().clear();
        plan.getAppQuotas().addAll(quotas);
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
        List<Long> planIds = page.getContent().stream().map(Plan::getId).toList();
        Map<Long, Long> appQuotaCounts = planRepository.countQuotas(planIds).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
        Set<Long> planIdsWithBonus = new HashSet<>(planRepository.plansWithBonuses(planIds));
        return PageResponse.from(page.map(plan -> planMapper.toPlanSummary(plan,
                appQuotaCounts.getOrDefault(plan.getId(), 0L), planIdsWithBonus.contains(plan.getId()))));
    }

    private Plan findPlanDetail(Long planId) {
        Plan plan = planRepository.findDetailWithBonuses(planId).orElseThrow(() -> planNotFound(planId));
        return planRepository.findDetailWithAppQuotas(planId).orElse(plan);
    }

    private AppException planNotFound(Long planId) {
        return new AppException(ErrorCode.PLAN_NOT_FOUND, "Plan " + planId + " not found");
    }

    private boolean isForeignKeyViolation(DataIntegrityViolationException exception) {
        Throwable cause = org.springframework.core.NestedExceptionUtils.getMostSpecificCause(exception);
        return cause instanceof SQLException sql && "23503".equals(sql.getSQLState());
    }
}
