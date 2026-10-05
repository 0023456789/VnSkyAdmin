package org.example.adminsky.dto.request;

import org.example.adminsky.enums.CutoffPolicy;

public interface PlanCutoffFields {
    CutoffPolicy getCutoffPolicy();

    Integer getThrottleSpeedKbps();
}
