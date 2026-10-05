package org.example.adminsky.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.adminsky.enums.CutoffPolicy;
import org.example.adminsky.enums.QuotaType;
import org.example.adminsky.validator.AllowedValuesConstraint;
import org.example.adminsky.validator.PlanCutoffConstraint;
import org.example.adminsky.validator.PlanQuotaConstraint;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@PlanQuotaConstraint
@PlanCutoffConstraint
public class PlanUpdateRequest implements PlanRequestFields {

    @NotBlank(message = "PLAN_CODE_INVALID")
    @Pattern(regexp = "^[A-Za-z0-9_-]{2,50}$", message = "PLAN_CODE_INVALID")
    String code;

    @NotBlank(message = "PLAN_NAME_INVALID")
    @Size(min = 2, max = 150, message = "PLAN_NAME_INVALID")
    String name;

    @Size(max = 1000, message = "PLAN_DESCRIPTION_INVALID")
    String description;

    @NotNull(message = "PLAN_PRICE_INVALID")
    @PositiveOrZero(message = "PLAN_PRICE_INVALID")
    @Max(value = 999_999_999_999L, message = "PLAN_PRICE_INVALID")
    Long price;

    @NotNull(message = "PLAN_DURATION_INVALID")
    @AllowedValuesConstraint(values = {1, 6, 12}, message = "PLAN_DURATION_INVALID")
    Integer durationMonths;

    @NotNull(message = "PLAN_QUOTA_INVALID")
    QuotaType quotaType;

    @NotNull(message = "PLAN_QUOTA_INVALID")
    @Positive(message = "PLAN_QUOTA_INVALID")
    Long dataQuotaMb;

    @Min(value = 1, message = "PLAN_QUOTA_INVALID")
    @Max(value = 32767, message = "PLAN_QUOTA_INVALID")
    Integer cycleDays;

    @PositiveOrZero(message = "PLAN_QUOTA_INVALID")
    Integer voiceMinutes;

    @NotNull(message = "PLAN_CUTOFF_INVALID")
    CutoffPolicy cutoffPolicy;

    @Positive(message = "PLAN_CUTOFF_INVALID")
    Integer throttleSpeedKbps;

    @NotNull(message = "PLAN_BONUS_INVALID")
    @Valid
    @Size(max = 2, message = "PLAN_BONUS_INVALID")
    List<@NotNull @Valid BonusRequest> firstCycleBonuses;

    @NotNull(message = "PLAN_APP_QUOTA_INVALID")
    @Valid
    @Size(max = 50, message = "PLAN_APP_QUOTA_INVALID")
    List<@NotNull @Valid AppQuotaRequest> appQuotas;
}
