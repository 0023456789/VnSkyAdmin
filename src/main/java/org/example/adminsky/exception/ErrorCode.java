package org.example.adminsky.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Invalid request", HttpStatus.BAD_REQUEST),
    INVALID_PAGE_REQUEST(1002, "Invalid page or sort request", HttpStatus.BAD_REQUEST),
    PLAN_NOT_FOUND(1101, "Plan not found", HttpStatus.NOT_FOUND),
    PLAN_CODE_EXISTED(1102, "Plan code already exists", HttpStatus.CONFLICT),
    PLAN_CODE_INVALID(1103, "Plan code must contain 2 to 50 letters, digits, underscores, or hyphens", HttpStatus.BAD_REQUEST),
    PLAN_NAME_INVALID(1104, "Plan name must contain at least {min} characters", HttpStatus.BAD_REQUEST),
    PLAN_PRICE_INVALID(1105, "Plan price must be a non-negative whole VND amount", HttpStatus.BAD_REQUEST),
    PLAN_DURATION_INVALID(1106, "Plan duration must be 1, 6, or 12 months", HttpStatus.BAD_REQUEST),
    PLAN_QUOTA_INVALID(1107, "Plan quota configuration is invalid", HttpStatus.BAD_REQUEST),
    PLAN_CUTOFF_INVALID(1108, "Plan cut-off configuration is invalid", HttpStatus.BAD_REQUEST),
    PLAN_BONUS_DUPLICATED(1109, "Bonus types must be unique", HttpStatus.BAD_REQUEST),
    PLAN_APP_QUOTA_DUPLICATED(1110, "App quota apps must be unique", HttpStatus.BAD_REQUEST),
    PLAN_IN_USE(1111, "Plan is referenced by another resource", HttpStatus.CONFLICT),
    PLAN_INACTIVE(1112, "Plan is inactive", HttpStatus.UNPROCESSABLE_ENTITY),
    PLAN_BONUS_INVALID(1113, "Plan bonus configuration is invalid", HttpStatus.BAD_REQUEST),
    PLAN_APP_QUOTA_INVALID(1114, "Plan app quota configuration is invalid", HttpStatus.BAD_REQUEST),
    PLAN_DESCRIPTION_INVALID(1115, "Plan description must be at most 1000 characters", HttpStatus.BAD_REQUEST),
    APP_NOT_FOUND(1201, "App not found", HttpStatus.NOT_FOUND),
    APP_CODE_EXISTED(1202, "App code already exists", HttpStatus.CONFLICT),
    APP_IN_USE(1203, "App is referenced by a plan quota", HttpStatus.CONFLICT),
    APP_INACTIVE(1204, "Inactive app cannot be assigned to a plan", HttpStatus.UNPROCESSABLE_ENTITY),
    APP_CODE_INVALID(1205, "App code must contain 2 to 50 letters, digits, underscores, or hyphens", HttpStatus.BAD_REQUEST),
    APP_NAME_INVALID(1206, "App name is invalid", HttpStatus.BAD_REQUEST),
    PROMO_NOT_FOUND(1301, "Promo code not found", HttpStatus.NOT_FOUND),
    PROMO_CODE_EXISTED(1302, "Promo code already exists", HttpStatus.CONFLICT),
    PROMO_IN_USE(1303, "Promo code is referenced by a subscription", HttpStatus.CONFLICT),
    PROMO_LIMIT_BELOW_USED(1304, "Usage limit cannot be lower than current usage", HttpStatus.CONFLICT),
    PROMO_LIMIT_BELOW_USAGE(1305, "Per-subscriber limit cannot be lower than existing usage", HttpStatus.CONFLICT),
    PROMO_DISCOUNT_INVALID(1306, "Promo discount configuration is invalid", HttpStatus.BAD_REQUEST),
    PROMO_DATE_RANGE_INVALID(1307, "Promo date range is invalid", HttpStatus.BAD_REQUEST),
    PROMO_SCOPE_INVALID(1308, "Promo plan scope is invalid", HttpStatus.BAD_REQUEST),
    PROMO_NOT_AVAILABLE(1309, "Promo code is not available", HttpStatus.UNPROCESSABLE_ENTITY),
    PROMO_CODE_INVALID(1310, "Promo code must contain 2 to 50 letters, digits, underscores, or hyphens", HttpStatus.BAD_REQUEST),
    PROMO_LIMIT_INVALID(1311, "Promo limit configuration is invalid", HttpStatus.BAD_REQUEST),
    SUBSCRIPTION_NOT_FOUND(1401, "Subscription not found", HttpStatus.NOT_FOUND),
    MSISDN_INVALID(1402, "MSISDN is invalid", HttpStatus.BAD_REQUEST),
    IDEMPOTENCY_KEY_INVALID(1403, "Idempotency-Key is missing or invalid", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus statusCode;

    ErrorCode(int code, String message, HttpStatus statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
