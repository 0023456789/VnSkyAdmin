package org.example.adminsky.validator;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.adminsky.dto.request.BonusRequest;
import org.example.adminsky.dto.request.PlanRequestFields;
import org.example.adminsky.exception.AppException;
import org.example.adminsky.exception.ErrorCode;
import org.example.adminsky.repository.PlanRepository;
import org.example.adminsky.util.CodeNormalizer;
import org.springframework.stereotype.Component;

/**
 * Kiểm tra tại tầng service cho các quy tắc cần truy vấn cơ sở dữ liệu (mã duy nhất)
 * hoặc không thể chỉ biểu đạt bằng Bean Validation (các phần tử con bị trùng).
 * Quy tắc hạn mức và thời điểm chốt được khai báo trên các DTO.
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlanRequestValidator {

    PlanRepository plans;

    public void validate(PlanRequestFields request, Long planId) {
        validateUniqueChildren(request);
        assertCodeAvailable(request.getCode(), planId);
    }

    private void validateUniqueChildren(PlanRequestFields request) {
        long distinctBonuses = request.getFirstCycleBonuses().stream().map(BonusRequest::getBonusType).distinct().count();
        if (distinctBonuses != request.getFirstCycleBonuses().size()) {
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
