package org.example.adminsky.service;

import lombok.RequiredArgsConstructor;
import org.example.adminsky.repository.PlanSubscriptionRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FirstCycleBonusResolver {
    private final PlanSubscriptionRepository subscriptionRepository;

    public boolean isNewSubscriber(String msisdn) {
        return !subscriptionRepository.existsByMsisdn(msisdn);
    }
}
