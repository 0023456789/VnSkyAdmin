package org.example.adminsky.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "promo_code_plan")
@Getter
@Setter
@NoArgsConstructor
public class PromoCodePlan {
    @EmbeddedId private PromoCodePlanId id = new PromoCodePlanId();
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @MapsId("promoCodeId") @JoinColumn(name = "promo_code_id", nullable = false)
    private PromoCode promoCode;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @MapsId("planId") @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;
}
