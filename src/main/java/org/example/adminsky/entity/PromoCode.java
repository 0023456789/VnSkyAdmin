package org.example.adminsky.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import org.example.adminsky.enums.DiscountType;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "promo_code")
@Getter
@Setter
@NoArgsConstructor
public class PromoCode {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50)
    private String code;
    @Column(length = 255)
    private String description;
    @Enumerated(EnumType.STRING) @Column(name = "discount_type", nullable = false, length = 20)
    private DiscountType discountType;
    @Column(name = "discount_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountValue;
    @Column(name = "max_discount_amount", precision = 12, scale = 2)
    private BigDecimal maxDiscountAmount;
    @Column(name = "min_order_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal minOrderAmount = BigDecimal.ZERO;
    @Column(name = "valid_from") private Instant validFrom;
    @Column(name = "valid_to") private Instant validTo;
    @Column(name = "usage_limit") private Integer usageLimit;
    @Column(name = "used_count", nullable = false) private Integer usedCount = 0;
    @Column(name = "is_active", nullable = false) private boolean active = true;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    @PrePersist void onCreate() { Instant now = Instant.now(); createdAt = now; updatedAt = now; }
    @PreUpdate void onUpdate() { updatedAt = Instant.now(); }
}
