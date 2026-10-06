package org.example.adminsky.constant;

import java.util.Map;

public final class SortFields {
    public static final Map<String, String> PLANS = Map.of(
            "createdAt", "createdAt", "name", "name", "code", "code", "price", "price",
            "durationMonths", "durationMonths");
    public static final Map<String, String> APP = Map.of(
            "createdAt", "createdAt", "name", "name", "code", "code");
    public static final Map<String, String> PROMO = Map.of(
            "createdAt", "createdAt", "code", "code", "validFrom", "validFrom", "validTo", "validTo");
    public static final Map<String, String> SUBSCRIPTION = Map.of(
            "createdAt", "createdAt", "activatedAt", "activatedAt", "expiresAt", "expiresAt",
            "msisdn", "msisdn", "status", "status", "finalPrice", "finalPrice");
    public static final String DEFAULT_SORT = "createdAt,desc";
    private SortFields() {
    }
}
