package org.example.adminsky.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.adminsky.enums.CutoffPolicy;
import org.example.adminsky.enums.QuotaType;

import java.time.Instant;
import java.util.List;

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
    Long price;
    Integer durationMonths;
    QuotaType quotaType;
    Long dataQuotaMb;
    Integer cycleDays;
    Integer voiceMinutes;
    CutoffPolicy cutoffPolicy;
    Integer throttleSpeedKbps;
    @JsonProperty("isActive")
    Boolean active;
    List<BonusResponse> bonuses;
    List<AppQuotaResponse> appQuotas;
    Instant createdAt;
    Instant updatedAt;
}
