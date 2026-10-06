package org.example.adminsky.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionExpirationScheduler {
    private final SubscriptionService subscriptionService;

    @Scheduled(fixedDelay = 60_000L)
    public void expireDueSubscriptions() {
        int updated = subscriptionService.expireDueSubscriptions();
        if (updated > 0) log.info("Marked {} subscriptions as expired", updated);
    }
}
