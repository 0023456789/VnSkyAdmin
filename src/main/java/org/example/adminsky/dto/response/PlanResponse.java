package org.example.adminsky.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.example.adminsky.enums.CutoffPolicy;
import org.example.adminsky.enums.QuotaType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanResponse {
    Long id;
    String code;
    String name;
    String description;
    BigDecimal price;
    Short durationMonths;
    QuotaType quotaType;
    Long dataQuotaMb;
    Short cycleDays;
    Integer voiceMinutes;
    CutoffPolicy cutoffPolicy;
    Integer throttleSpeedKbps;
    Boolean active;
    Instant createdAt;
    Instant updatedAt;
    List<BonusResponse> bonuses;
    List<AppQuotaResponse> appQuotas;
}
