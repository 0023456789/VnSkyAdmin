package org.example.adminsky.repository.specification;

import org.example.adminsky.entity.PlanSubscription;
import org.example.adminsky.enums.SubscriptionStatus;
import org.springframework.data.jpa.domain.Specification;

/** Bộ lọc danh sách subscription; tham số null hoặc rỗng sẽ không thêm điều kiện lọc. */
public final class SubscriptionSpecifications {

    public static final String MSISDN = "msisdn";
    public static final String STATUS = "status";

    private SubscriptionSpecifications() {
    }

    public static Specification<PlanSubscription> filter(String msisdn, SubscriptionStatus status) {
        return Specification.allOf(hasMsisdn(msisdn), hasStatus(status));
    }

    public static Specification<PlanSubscription> hasMsisdn(String msisdn) {
        return (root, query, cb) ->
                msisdn == null || msisdn.isBlank() ? cb.conjunction() : cb.equal(root.get(MSISDN), msisdn.trim());
    }

    public static Specification<PlanSubscription> hasStatus(SubscriptionStatus status) {
        return (root, query, cb) -> status == null ? cb.conjunction() : cb.equal(root.get(STATUS), status);
    }
}
