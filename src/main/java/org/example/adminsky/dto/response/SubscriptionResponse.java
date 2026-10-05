package org.example.adminsky.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.adminsky.enums.SubscriptionStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionResponse {
    Long id;
    Long planId;
    String msisdn;
    String promoCode;
    Long originalPrice;
    Long discountAmount;
    Long finalPrice;
    @JsonProperty("isFirstCycle")
    boolean firstCycle;
    List<BonusResponse> bonuses;
    SubscriptionStatus status;
    Instant activatedAt;
    Instant expiresAt;
    Instant createdAt;
}
