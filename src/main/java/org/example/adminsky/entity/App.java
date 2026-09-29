package org.example.adminsky.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "app")
@Getter
@Setter
@NoArgsConstructor
public class App {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 50)
    private String code;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
