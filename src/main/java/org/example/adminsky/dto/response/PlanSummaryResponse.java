package org.example.adminsky.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.adminsky.enums.CutoffPolicy;
import org.example.adminsky.enums.QuotaType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanSummaryResponse {

    Long id;
    String code;
    String name;
    Long price;
    Integer durationMonths;
    QuotaType quotaType;
    Long dataQuotaMb;
    CutoffPolicy cutoffPolicy;
    @JsonProperty("isActive")
    Boolean active;
    long appQuotaCount;
    boolean hasBonus;
    Instant createdAt;
}
