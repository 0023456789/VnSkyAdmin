package org.example.adminsky.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.adminsky.enums.SubscriptionStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "plan_subscription", uniqueConstraints = @UniqueConstraint(
        name = "uq_sub_idempotency", columnNames = {"msisdn", "idempotency_key"}))
@Getter
@Setter
@NoArgsConstructor
public class PlanSubscription extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(nullable = false, length = 15)
    private String msisdn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promo_code_id")
    private PromoCode promoCode;

    @Column(name = "original_price", nullable = false, precision = 12, scale = 0)
    private BigDecimal originalPrice;

    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 0)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "final_price", nullable = false, precision = 12, scale = 0)
    private BigDecimal finalPrice;

    @Column(name = "is_first_cycle", nullable = false)
    private boolean firstCycle;

    @Column(name = "promo_single_use", nullable = false)
    private boolean promoSingleUse;

    @Column(name = "idempotency_key", nullable = false, length = 64)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;

    @Column(name = "activated_at")
    private Instant activatedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PlanSubscriptionBonus> grantedBonuses = new LinkedHashSet<>();
}
