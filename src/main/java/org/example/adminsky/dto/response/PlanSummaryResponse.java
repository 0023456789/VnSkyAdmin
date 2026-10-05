package org.example.adminsky.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
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
    Integer cycleDays;
    @JsonProperty("isActive")
    Boolean active;
}
