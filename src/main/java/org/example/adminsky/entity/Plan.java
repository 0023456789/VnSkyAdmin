package org.example.adminsky.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.adminsky.enums.CutoffPolicy;
import org.example.adminsky.enums.QuotaType;
import org.example.adminsky.util.CodeNormalizer;

@Entity
@Table(name = "plan")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Plan extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 50)
    String code;

    @Column(nullable = false, length = 150)
    String name;

    @Column(columnDefinition = "text")
    String description;

    @Column(nullable = false, precision = 12, scale = 0)
    BigDecimal price;

    @Column(name = "duration_months", nullable = false)
    Short durationMonths;

    @Enumerated(EnumType.STRING)
    @Column(name = "quota_type", nullable = false, length = 20)
    QuotaType quotaType;

    @Column(name = "data_quota_mb", nullable = false)
    Long dataQuotaMb;

    @Column(name = "cycle_days")
    Short cycleDays;

    @Builder.Default
    @Column(name = "voice_minutes", nullable = false)
    Integer voiceMinutes = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "cutoff_policy", nullable = false, length = 20)
    CutoffPolicy cutoffPolicy;

    @Column(name = "throttle_speed_kbps")
    Integer throttleSpeedKbps;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    boolean active = true;

    @Builder.Default
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    Set<PlanFirstCycleBonus> bonuses = new LinkedHashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    Set<PlanAppQuota> appQuotas = new LinkedHashSet<>();

    public void addBonus(PlanFirstCycleBonus bonus) {
        bonuses.add(bonus);
        bonus.setPlan(this);
    }

    public void removeBonus(PlanFirstCycleBonus bonus) {
        bonuses.remove(bonus);
        bonus.setPlan(null);
    }

    public void addAppQuota(PlanAppQuota quota) {
        appQuotas.add(quota);
        quota.setPlan(this);
    }

    public void removeAppQuota(PlanAppQuota quota) {
        appQuotas.remove(quota);
        quota.setPlan(null);
    }

    @PrePersist
    @PreUpdate
    void normalize() {
        code = CodeNormalizer.normalize(code);
    }
}
