package org.example.adminsky.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import lombok.experimental.SuperBuilder;
import org.example.adminsky.enums.CutoffPolicy;
import org.example.adminsky.enums.QuotaType;
import org.example.adminsky.validator.AllowedValuesConstraint;
import org.example.adminsky.validator.PlanCutoffConstraint;
import org.example.adminsky.validator.PlanQuotaConstraint;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@PlanQuotaConstraint(message = "PLAN_QUOTA_INVALID")
@PlanCutoffConstraint(message = "PLAN_CUTOFF_INVALID")
public abstract class PlanRequest {
    @NotBlank(message = "PLAN_CODE_INVALID")
    @Pattern(regexp = "^[A-Za-z0-9_-]{2,50}$", message = "PLAN_CODE_INVALID")
    String code;

    @NotBlank(message = "PLAN_NAME_INVALID")
    @Size(min = 2, max = 150, message = "PLAN_NAME_INVALID")
    String name;

    String description;

    @NotNull(message = "PLAN_PRICE_INVALID")
    @PositiveOrZero(message = "PLAN_PRICE_INVALID")
    Long price;

    @NotNull(message = "PLAN_DURATION_INVALID")
    @AllowedValuesConstraint(values = {1, 6, 12}, message = "PLAN_DURATION_INVALID")
    Integer durationMonths;

    @NotNull(message = "PLAN_QUOTA_INVALID")
    QuotaType quotaType;

    @NotNull(message = "PLAN_QUOTA_INVALID")
    @Positive(message = "PLAN_QUOTA_INVALID")
    Long dataQuotaMb;

    @Positive(message = "PLAN_QUOTA_INVALID")
    Integer cycleDays;

    @NotNull(message = "PLAN_QUOTA_INVALID")
    @PositiveOrZero(message = "PLAN_QUOTA_INVALID")
    Integer voiceMinutes;

    @NotNull(message = "PLAN_CUTOFF_INVALID")
    CutoffPolicy cutoffPolicy;

    @Positive(message = "PLAN_CUTOFF_INVALID")
    Integer throttleSpeedKbps;

    @NotNull(message = "PLAN_BONUS_DUPLICATED")
    @Valid
    List<@NotNull @Valid BonusRequest> bonuses;

    @NotNull(message = "PLAN_APP_QUOTA_DUPLICATED")
    @Valid
    List<@NotNull @Valid AppQuotaRequest> appQuotas;
}
