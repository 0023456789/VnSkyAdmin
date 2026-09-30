package org.example.adminsky.constant;

import java.util.Map;

public final class SortFields {
    private SortFields() {}
    public static final Map<String, String> PLANS = Map.of(
            "createdAt", "createdAt", "name", "name", "code", "code", "price", "price",
            "durationMonths", "durationMonths");
}
