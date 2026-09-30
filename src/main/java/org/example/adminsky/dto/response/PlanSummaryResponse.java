package org.example.adminsky.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.adminsky.enums.QuotaType;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanSummaryResponse {
    Long id;
    String code;
    String name;
    BigDecimal price;
    Short durationMonths;
    QuotaType quotaType;
    Long dataQuotaMb;
    Boolean active;
    Instant createdAt;
    long appQuotaCount;
    boolean hasBonus;
}
