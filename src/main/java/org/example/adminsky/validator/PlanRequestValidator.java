package org.example.adminsky.validator;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.example.adminsky.dto.request.PlanRequest;
import org.example.adminsky.dto.request.BonusRequest;
import org.example.adminsky.enums.CutoffPolicy;
import org.example.adminsky.enums.QuotaType;
import org.example.adminsky.exception.AppException;
import org.example.adminsky.exception.ErrorCode;
import org.example.adminsky.repository.PlanRepository;
import org.springframework.stereotype.Component;
import org.example.adminsky.util.CodeNormalizer;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlanRequestValidator {
    PlanRepository plans;

    public void validate(PlanRequest request, Long planId) {
        validatePlanRules(request);
        validateUniqueChildren(request);
        assertCodeAvailable(request.getCode(), planId);
    }

    private void validatePlanRules(PlanRequest request) {
        Integer durationMonths = request.getDurationMonths();
        if (durationMonths == null) throw new AppException(ErrorCode.PLAN_DURATION_INVALID);
        int months = durationMonths;
        if (months != 1 && months != 6 && months != 12) {
            throw new AppException(ErrorCode.PLAN_DURATION_INVALID);
        }
        if (request.getQuotaType() == QuotaType.PER_CYCLE) {
            if (request.getCycleDays() == null || request.getCycleDays() > months * 30) {
                throw new AppException(ErrorCode.PLAN_QUOTA_INVALID);
            }
        } else if (request.getCycleDays() != null) {
            throw new AppException(ErrorCode.PLAN_QUOTA_INVALID);
        }
        if (request.getCutoffPolicy() == CutoffPolicy.THROTTLE && request.getThrottleSpeedKbps() == null) {
            throw new AppException(ErrorCode.PLAN_CUTOFF_INVALID);
        }
        if (request.getCutoffPolicy() == CutoffPolicy.DISCONNECT && request.getThrottleSpeedKbps() != null) {
            throw new AppException(ErrorCode.PLAN_CUTOFF_INVALID);
        }
    }

    private void validateUniqueChildren(PlanRequest request) {
        long distinctBonuses = request.getBonuses().stream().map(BonusRequest::getBonusType).distinct().count();
        if (distinctBonuses != request.getBonuses().size()) {
            throw new AppException(ErrorCode.PLAN_BONUS_DUPLICATED);
        }
        long distinctApps = request.getAppQuotas().stream().map(item -> item.getAppId()).distinct().count();
        if (distinctApps != request.getAppQuotas().size()) {
            throw new AppException(ErrorCode.PLAN_APP_QUOTA_DUPLICATED);
        }
    }

    private void assertCodeAvailable(String inputCode, Long planId) {
        String code = CodeNormalizer.normalize(inputCode);
        boolean exists = planId == null
                ? plans.existsByCode(code)
                : plans.existsByCodeAndIdNot(code, planId);
        if (exists) {
            throw new AppException(ErrorCode.PLAN_CODE_EXISTED);
        }
    }

}
