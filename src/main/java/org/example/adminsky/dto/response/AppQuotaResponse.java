package org.example.adminsky.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.adminsky.enums.QuotaType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppQuotaResponse {
    Long appId;
    String appCode;
    String appName;
    QuotaType quotaType;
    Long quotaMb;
}
