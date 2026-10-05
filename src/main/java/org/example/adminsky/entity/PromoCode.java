package org.example.adminsky.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.adminsky.enums.DiscountType;
import org.example.adminsky.util.CodeNormalizer;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "promo_code")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromoCode extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(nullable = false, length = 50)
    String code;
    @Column(length = 255)
    String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    DiscountType discountType;
    @Column(name = "discount_value", nullable = false, precision = 12, scale = 2)
    BigDecimal discountValue;
    @Column(name = "max_discount_amount", precision = 12, scale = 0)
    BigDecimal maxDiscountAmount;
    @Builder.Default
    @Column(name = "min_order_amount", nullable = false, precision = 12, scale = 0)
    BigDecimal minOrderAmount = BigDecimal.ZERO;
    @Column(name = "valid_from")
    Instant validFrom;
    @Column(name = "valid_to")
    Instant validTo;
    @Column(name = "usage_limit")
    Integer usageLimit;
    @Builder.Default
    @Column(name = "used_count", nullable = false)
    Integer usedCount = 0;
    @Column(name = "max_uses_per_msisdn")
    Integer maxUsesPerMsisdn;
    @Builder.Default
    @Column(name = "applies_to_all_plans", nullable = false)
    boolean appliesToAllPlans = true;
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    boolean active = true;
    @ManyToMany
    @JoinTable(name = "promo_code_plan", joinColumns = @JoinColumn(name = "promo_code_id"),
            inverseJoinColumns = @JoinColumn(name = "plan_id"))
    @Builder.Default
    Set<Plan> plans = new LinkedHashSet<>();

    @PrePersist
    @PreUpdate
    void normalize() {
        code = CodeNormalizer.normalize(code);
    }
}
