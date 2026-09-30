package org.example.adminsky.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.adminsky.enums.BonusType;

@Entity
@Table(name = "plan_subscription_bonus", uniqueConstraints = @UniqueConstraint(
        name = "uq_sub_bonus", columnNames = {"subscription_id", "bonus_type"}))
@Getter
@Setter
@NoArgsConstructor
public class PlanSubscriptionBonus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subscription_id", nullable = false)
    private PlanSubscription subscription;

    @Enumerated(EnumType.STRING)
    @Column(name = "bonus_type", nullable = false, length = 20)
    private BonusType bonusType;

    @Column(nullable = false)
    private Long amount;
}
