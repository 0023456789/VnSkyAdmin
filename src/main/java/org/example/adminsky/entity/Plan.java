package org.example.adminsky.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import org.example.adminsky.enums.CutoffPolicy;
import org.example.adminsky.enums.QuotaType;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "plan")
@Getter
@Setter
@NoArgsConstructor
public class Plan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 50)
    private String code;
    @Column(nullable = false, length = 150)
    private String name;
    @Column(columnDefinition = "text")
    private String description;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    @Column(name = "duration_months", nullable = false)
    private Short durationMonths;
    @Enumerated(EnumType.STRING) @Column(name = "quota_type", nullable = false, length = 20)
    private QuotaType quotaType;
    @Column(name = "data_quota_mb", nullable = false)
    private Long dataQuotaMb;
    @Column(name = "cycle_days")
    private Short cycleDays;
    @Column(name = "voice_minutes", nullable = false)
    private Integer voiceMinutes = 0;
    @Enumerated(EnumType.STRING) @Column(name = "cutoff_policy", nullable = false, length = 20)
    private CutoffPolicy cutoffPolicy;
    @Column(name = "throttle_speed_kbps")
    private Integer throttleSpeedKbps;
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist void onCreate() { Instant now = Instant.now(); createdAt = now; updatedAt = now; }
    @PreUpdate void onUpdate() { updatedAt = Instant.now(); }
}
