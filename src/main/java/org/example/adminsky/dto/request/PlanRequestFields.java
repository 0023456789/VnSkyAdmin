package org.example.adminsky.dto.request;

import java.util.List;

public interface PlanRequestFields extends PlanQuotaFields, PlanCutoffFields {
    String getCode();

    Long getPrice();

    Long getDataQuotaMb();

    Integer getVoiceMinutes();

    List<BonusRequest> getFirstCycleBonuses();

    List<AppQuotaRequest> getAppQuotas();
}
