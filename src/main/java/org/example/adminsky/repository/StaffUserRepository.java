package org.example.adminsky.repository;

import java.util.Optional;
import org.example.adminsky.entity.StaffUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffUserRepository extends JpaRepository<StaffUser, Long> {
    Optional<StaffUser> findByUsername(String username);

    @EntityGraph(attributePaths = "roles")
    Optional<StaffUser> findWithRolesByUsername(String username);
}
