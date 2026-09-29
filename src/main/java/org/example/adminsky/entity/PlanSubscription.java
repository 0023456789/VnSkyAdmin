package org.example.adminsky.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import org.example.adminsky.enums.SubscriptionStatus;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "plan_subscription")
@Getter
@Setter
@NoArgsConstructor
public class PlanSubscription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;
    @Column(nullable = false, length = 15) private String msisdn;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "promo_code_id") private PromoCode promoCode;
    @Column(name = "original_price", nullable = false, precision = 12, scale = 2) private BigDecimal originalPrice;
    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2) private BigDecimal discountAmount = BigDecimal.ZERO;
    @Column(name = "final_price", nullable = false, precision = 12, scale = 2) private BigDecimal finalPrice;
    @Column(name = "is_first_cycle", nullable = false) private boolean firstCycle;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private SubscriptionStatus status = SubscriptionStatus.ACTIVE;
    @Column(name = "activated_at") private Instant activatedAt;
    @Column(name = "expires_at") private Instant expiresAt;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @PrePersist void onCreate() { if (createdAt == null) createdAt = Instant.now(); }
}
