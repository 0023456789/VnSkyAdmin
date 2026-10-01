package org.example.adminsky.constant;

import java.util.Map;

public final class SortFields {
    private SortFields() {}
    public static final Map<String, String> PLANS = Map.of(
            "createdAt", "createdAt", "name", "name", "code", "code", "price", "price",
            "durationMonths", "durationMonths");
    public static final Map<String, String> APP = Map.of(
            "createdAt", "createdAt", "name", "name", "code", "code");
    public static final Map<String, String> PROMO = Map.of(
            "createdAt", "createdAt", "code", "code", "validFrom", "validFrom", "validTo", "validTo");
    public static final String DEFAULT_SORT = "createdAt,desc";
}
