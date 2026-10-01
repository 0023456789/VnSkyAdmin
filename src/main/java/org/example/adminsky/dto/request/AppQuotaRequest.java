package org.example.adminsky.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.adminsky.enums.QuotaType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppQuotaRequest {

    @NotNull(message = "PLAN_APP_QUOTA_INVALID")
    Long appId;

    @NotNull(message = "PLAN_APP_QUOTA_INVALID")
    QuotaType quotaType;

    @NotNull(message = "PLAN_APP_QUOTA_INVALID")
    @Positive(message = "PLAN_APP_QUOTA_INVALID")
    Long quotaMb;
}
