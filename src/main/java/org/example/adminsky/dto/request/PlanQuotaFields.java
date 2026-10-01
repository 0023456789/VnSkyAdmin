package org.example.adminsky.dto.request;

import org.example.adminsky.enums.QuotaType;

public interface PlanQuotaFields {
    QuotaType getQuotaType();
    Integer getCycleDays();
    Integer getDurationMonths();
}
