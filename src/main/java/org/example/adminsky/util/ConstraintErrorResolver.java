package org.example.adminsky.util;

import org.example.adminsky.exception.ErrorCode;
import org.postgresql.util.PSQLException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import java.sql.SQLException;

@Component
public class ConstraintErrorResolver {
    public ErrorCode resolve(DataIntegrityViolationException exception) {
        Throwable cause = NestedExceptionUtils.getMostSpecificCause(exception);
        String constraint = cause instanceof PSQLException pg && pg.getServerErrorMessage() != null
                ? pg.getServerErrorMessage().getConstraint() : null;
        String state = cause instanceof SQLException sql ? sql.getSQLState() : null;

        if ("uq_plan_code".equals(constraint)) return ErrorCode.PLAN_CODE_EXISTED;
        if ("uq_app_code".equals(constraint)) return ErrorCode.APP_CODE_EXISTED;
        if ("uq_promo_code".equals(constraint)) return ErrorCode.PROMO_CODE_EXISTED;
        if ("23503".equals(state)) {
            if ("plan_app_quota_app_id_fkey".equals(constraint)) return ErrorCode.APP_IN_USE;
            if ("plan_subscription_plan_id_fkey".equals(constraint)) return ErrorCode.PLAN_IN_USE;
            if ("plan_subscription_promo_code_id_fkey".equals(constraint)) return ErrorCode.PROMO_IN_USE;
        }
        return ErrorCode.UNCATEGORIZED_EXCEPTION;
    }
}
