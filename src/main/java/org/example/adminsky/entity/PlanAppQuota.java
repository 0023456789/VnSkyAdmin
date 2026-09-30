package org.example.adminsky.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import org.example.adminsky.enums.QuotaType;

@Entity
@Table(name = "plan_app_quota", uniqueConstraints = @UniqueConstraint(name = "uq_plan_app", columnNames = {"plan_id", "app_id"}))
@Getter
@Setter
@NoArgsConstructor
public class PlanAppQuota {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "app_id", nullable = false, foreignKey = @ForeignKey(name = "fk_plan_app_quota_app"))
    private App app;
    @Enumerated(EnumType.STRING) @Column(name = "quota_type", nullable = false, length = 20)
    private QuotaType quotaType;
    @Column(name = "quota_mb", nullable = false)
    private Long quotaMb;
}
