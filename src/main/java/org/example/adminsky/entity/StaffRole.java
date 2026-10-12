package org.example.adminsky.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "staff_role")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class StaffRole {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 30, unique = true)
    private String code;
    @Column(nullable = false, length = 100)
    private String name;
}
