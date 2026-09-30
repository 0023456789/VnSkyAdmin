package org.example.adminsky.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.adminsky.enums.QuotaType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppQuotaRequest {
    @NotNull(message = "PLAN_APP_QUOTA_DUPLICATED")
    @Positive(message = "PLAN_APP_QUOTA_DUPLICATED")
    Long appId;
    @NotNull(message = "PLAN_APP_QUOTA_DUPLICATED")
    QuotaType quotaType;
    @NotNull(message = "PLAN_APP_QUOTA_DUPLICATED")
    @Positive(message = "PLAN_APP_QUOTA_DUPLICATED")
    Long quotaMb;
}
