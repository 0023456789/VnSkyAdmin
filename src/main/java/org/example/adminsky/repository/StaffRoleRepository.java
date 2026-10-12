package org.example.adminsky.repository;

import java.util.Optional;
import org.example.adminsky.entity.StaffRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRoleRepository extends JpaRepository<StaffRole, Long> {
    Optional<StaffRole> findByCode(String code);
}
