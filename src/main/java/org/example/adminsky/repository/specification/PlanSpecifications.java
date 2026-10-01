package org.example.adminsky.repository.specification;

import org.example.adminsky.entity.Plan;
import org.springframework.data.jpa.domain.Specification;
import java.util.Locale;

public final class PlanSpecifications {
    private PlanSpecifications() {}

    public static Specification<Plan> hasActive(Boolean active) {
        return (root, query, cb) -> active == null ? cb.conjunction() : cb.equal(root.get("active"), active);
    }

    public static Specification<Plan> hasDuration(Integer durationMonths) {
        if (durationMonths == null) return (root, query, cb) -> cb.conjunction();
        if (durationMonths < Short.MIN_VALUE || durationMonths > Short.MAX_VALUE) {
            return (root, query, cb) -> cb.disjunction();
        }
        return (root, query, cb) -> cb.equal(root.get("durationMonths"), durationMonths.shortValue());
    }

    public static Specification<Plan> keywordLike(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return (root, query, cb) -> cb.conjunction();
        String escaped = keyword.trim().toLowerCase(Locale.ROOT)
                .replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("name")), "%" + escaped + "%", '\\'),
                cb.like(cb.lower(root.get("code")), "%" + escaped + "%", '\\'));
    }
}
