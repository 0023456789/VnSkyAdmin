package org.example.adminsky.dto.request;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.adminsky.enums.DiscountType;
import org.example.adminsky.validator.PromoDiscountConstraint;
import org.example.adminsky.validator.PromoScopeConstraint;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@PromoDiscountConstraint(message = "PROMO_DISCOUNT_INVALID")
@PromoScopeConstraint(message = "PROMO_SCOPE_INVALID")
public class PromoCreationRequest implements PromoDiscountFields, PromoScopeFields {
    @NotBlank(message = "PROMO_CODE_INVALID")
    @Pattern(regexp = "^[A-Za-z0-9_-]{2,50}$", message = "PROMO_CODE_INVALID")
    String code;

    @Size(max = 255, message = "PROMO_CODE_INVALID")
    String description;

    @NotNull(message = "PROMO_DISCOUNT_INVALID")
    DiscountType discountType;

    @NotNull(message = "PROMO_DISCOUNT_INVALID")
    @DecimalMin(value = "0", inclusive = false, message = "PROMO_DISCOUNT_INVALID")
    @Digits(integer = 10, fraction = 2, message = "PROMO_DISCOUNT_INVALID")
    BigDecimal discountValue;

    @Positive(message = "PROMO_DISCOUNT_INVALID")
    @Max(value = 999_999_999_999L, message = "PROMO_DISCOUNT_INVALID")
    Long maxDiscountAmount;

    @Builder.Default
    @JsonSetter(nulls = Nulls.SKIP)
    @PositiveOrZero(message = "PROMO_LIMIT_INVALID")
    @Max(value = 999_999_999_999L, message = "PROMO_LIMIT_INVALID")
    Long minOrderAmount = 0L;

    Instant validFrom;
    Instant validTo;

    @Positive(message = "PROMO_LIMIT_INVALID")
    Integer usageLimit;

    @Positive(message = "PROMO_LIMIT_INVALID")
    Integer maxUsesPerMsisdn;

    @NotNull(message = "PROMO_SCOPE_INVALID")
    Boolean appliesToAllPlans;

    List<Long> planIds;
}
