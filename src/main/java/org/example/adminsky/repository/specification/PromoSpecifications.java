package org.example.adminsky.repository.specification;

import java.util.Locale;
import org.example.adminsky.entity.PromoCode;
import org.springframework.data.jpa.domain.Specification;

public final class PromoSpecifications {
    private PromoSpecifications() {}

    public static Specification<PromoCode> filter(Boolean active, String keyword) {
        return hasActive(active).and(keywordLike(keyword));
    }

    public static Specification<PromoCode> hasActive(Boolean active) {
        return (root, query, cb) -> active == null ? cb.conjunction() : cb.equal(root.get("active"), active);
    }

    public static Specification<PromoCode> keywordLike(String keyword) {
        if (keyword == null || keyword.isBlank()) return (root, query, cb) -> cb.conjunction();
        String escaped = keyword.trim().toLowerCase(Locale.ROOT)
                .replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("code")), "%" + escaped + "%", '\\'),
                cb.like(cb.lower(root.get("description")), "%" + escaped + "%", '\\'));
    }
}
