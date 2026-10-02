package org.example.adminsky.dto.request;

import java.util.List;

public interface PromoScopeFields {
    Boolean getAppliesToAllPlans();
    List<Long> getPlanIds();
}
