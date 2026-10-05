package org.example.adminsky.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.adminsky.enums.DiscountType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromoResponse {
    Long id;
    String code;
    String description;
    DiscountType discountType;
    BigDecimal discountValue;
    Long maxDiscountAmount;
    Long minOrderAmount;
    Instant validFrom;
    Instant validTo;
    Integer usageLimit;
    Integer usedCount;
    Integer maxUsesPerMsisdn;
    Boolean appliesToAllPlans;
    @JsonProperty("isActive")
    Boolean active;
    Set<Long> planIds;
    Instant createdAt;
    Instant updatedAt;
}
