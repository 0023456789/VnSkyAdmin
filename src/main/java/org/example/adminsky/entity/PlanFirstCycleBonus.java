package org.example.adminsky.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;
import org.example.adminsky.enums.BonusType;

@Entity
@Table(name = "plan_first_cycle_bonus", uniqueConstraints = @UniqueConstraint(name = "uq_plan_bonus", columnNames = {"plan_id", "bonus_type"}))
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanFirstCycleBonus {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;
    @Enumerated(EnumType.STRING) @Column(name = "bonus_type", nullable = false, length = 20)
    private BonusType bonusType;
    @Column(nullable = false)
    private Long amount;
}
