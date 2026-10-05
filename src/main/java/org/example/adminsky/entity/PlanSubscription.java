package org.example.adminsky.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.adminsky.enums.SubscriptionStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "plan_subscription")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanSubscription extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    Plan plan;

    @Column(nullable = false, length = 15)
    String msisdn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promo_code_id")
    PromoCode promoCode;

    @Column(name = "original_price", nullable = false, precision = 12, scale = 0)
    BigDecimal originalPrice;

    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 0)
    @Builder.Default
    BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "final_price", nullable = false, precision = 12, scale = 0)
    BigDecimal finalPrice;

    @Column(name = "is_first_cycle", nullable = false)
    boolean firstCycle;

    @Column(name = "promo_single_use", nullable = false)
    @Builder.Default
    boolean promoSingleUse = false;

    @Column(name = "idempotency_key", nullable = false, length = 64)
    String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    SubscriptionStatus status = SubscriptionStatus.ACTIVE;

    @Column(name = "activated_at")
    Instant activatedAt;

    @Column(name = "expires_at")
    Instant expiresAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "subscription", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    Set<PlanSubscriptionBonus> bonuses = new LinkedHashSet<>();

    public void addBonus(PlanSubscriptionBonus bonus) {
        bonuses.add(bonus);
        bonus.setSubscription(this);
    }

    public void removeBonus(PlanSubscriptionBonus bonus) {
        bonuses.remove(bonus);
        bonus.setSubscription(null);
    }
}
