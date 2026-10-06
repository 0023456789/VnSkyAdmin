package org.example.adminsky.mapper;

import org.example.adminsky.dto.response.BonusResponse;
import org.example.adminsky.dto.response.SubscriptionResponse;
import org.example.adminsky.entity.PlanSubscription;
import org.example.adminsky.entity.PlanSubscriptionBonus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    @Mapping(source = "plan.id", target = "planId")
    @Mapping(source = "promoCode.code", target = "promoCode")
    @Mapping(source = "bonuses", target = "bonuses")
    SubscriptionResponse toSubscriptionResponse(PlanSubscription subscription);

    BonusResponse toBonusResponse(PlanSubscriptionBonus bonus);
}
