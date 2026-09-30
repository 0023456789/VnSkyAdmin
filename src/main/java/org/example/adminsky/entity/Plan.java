package org.example.adminsky.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.example.adminsky.enums.CutoffPolicy;
import org.example.adminsky.enums.QuotaType;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "plan", uniqueConstraints = @UniqueConstraint(name = "uq_plan_code", columnNames = "code"))
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Plan extends BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(nullable = false, precision = 12, scale = 0)
    private BigDecimal price;

    @Column(name = "duration_months", nullable = false)
    private Short durationMonths;

    @Enumerated(EnumType.STRING)
    @Column(name = "quota_type", nullable = false, length = 20)
    private QuotaType quotaType;

    @Column(name = "data_quota_mb", nullable = false)
    private Long dataQuotaMb;

    @Column(name = "cycle_days")
    private Short cycleDays;

    @Column(name = "voice_minutes", nullable = false)
    @Builder.Default
    Integer voiceMinutes = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "cutoff_policy", nullable = false, length = 20)
    private CutoffPolicy cutoffPolicy;

    @Column(name = "throttle_speed_kbps")
    private Integer throttleSpeedKbps;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    boolean active = true;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    Set<PlanFirstCycleBonus> bonuses = new LinkedHashSet<>();

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    Set<PlanAppQuota> appQuotas = new LinkedHashSet<>();
}
