package org.example.adminsky.service;

import java.time.Instant;
import java.time.ZoneId;
import org.springframework.stereotype.Component;

@Component
public class PlanExpiryCalculator {
    private static final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    public Instant expiresAt(Instant activatedAt, int months) {
        return activatedAt.atZone(VIETNAM_ZONE).plusMonths(months).toInstant();
    }
}
