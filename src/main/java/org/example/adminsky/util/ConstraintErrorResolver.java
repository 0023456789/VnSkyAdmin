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
        if ("uq_plan_bonus".equals(constraint)) return ErrorCode.PLAN_BONUS_DUPLICATED;
        if ("uq_plan_app".equals(constraint)) return ErrorCode.PLAN_APP_QUOTA_DUPLICATED;
        if ("uq_app_code".equals(constraint)) return ErrorCode.APP_CODE_EXISTED;
        if ("uq_promo_code".equals(constraint)) return ErrorCode.PROMO_CODE_EXISTED;
        if ("uq_sub_idempotency".equals(constraint)) return ErrorCode.IDEMPOTENCY_KEY_REUSED;

        // PostgreSQL CHECK constraints are also reported as SQLSTATE 23514.
        // Translate the named constraints to the same domain errors used by
        // request validation, so a race or persistence-level rejection remains
        // actionable to API clients.
        if ("23514".equals(state)) {
            return switch (constraint == null ? "" : constraint) {
                case "ck_plan_code_norm" -> ErrorCode.PLAN_CODE_INVALID;
                case "ck_plan_cycle_days", "ck_plan_cycle_fits" -> ErrorCode.PLAN_QUOTA_INVALID;
                case "ck_plan_throttle" -> ErrorCode.PLAN_CUTOFF_INVALID;
                case "ck_app_code_norm" -> ErrorCode.APP_CODE_INVALID;
                case "ck_promo_code_norm" -> ErrorCode.PROMO_CODE_INVALID;
                case "ck_promo_percent", "ck_promo_fixed_whole", "ck_promo_cap_percent_only" -> ErrorCode.PROMO_DISCOUNT_INVALID;
                case "ck_promo_dates" -> ErrorCode.PROMO_DATE_RANGE_INVALID;
                case "ck_promo_usage" -> ErrorCode.PROMO_LIMIT_INVALID;
                case "plan_subscription_msisdn_check" -> ErrorCode.MSISDN_INVALID;
                default -> ErrorCode.UNCATEGORIZED_EXCEPTION;
            };
        }
        if ("23503".equals(state)) {
            if ("plan_app_quota_app_id_fkey".equals(constraint)) return ErrorCode.APP_IN_USE;
            if ("plan_subscription_plan_id_fkey".equals(constraint)) return ErrorCode.PLAN_IN_USE;
            if ("plan_subscription_promo_code_id_fkey".equals(constraint)) return ErrorCode.PROMO_IN_USE;
        }
        return ErrorCode.UNCATEGORIZED_EXCEPTION;
    }
}
