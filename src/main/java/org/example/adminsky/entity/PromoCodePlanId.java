package org.example.adminsky.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class PromoCodePlanId implements Serializable {
    @Column(name = "promo_code_id") private Long promoCodeId;
    @Column(name = "plan_id") private Long planId;
    public PromoCodePlanId() {}
    @Override public boolean equals(Object other) { if (this == other) return true; if (!(other instanceof PromoCodePlanId that)) return false; return Objects.equals(promoCodeId, that.promoCodeId) && Objects.equals(planId, that.planId); }
    @Override public int hashCode() { return Objects.hash(promoCodeId, planId); }
}
