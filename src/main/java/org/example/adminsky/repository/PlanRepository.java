package org.example.adminsky.repository;

import org.example.adminsky.entity.Plan;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long>, JpaSpecificationExecutor<Plan> {
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Long id);
    // Serializes plan edits with any registration holding a PESSIMISTIC_READ lock.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Plan> findForUpdateById(Long id);
    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Plan> findForShareById(Long id);

    @EntityGraph(attributePaths = "bonuses")
    Optional<Plan> findWithBonusesById(Long id);

    @EntityGraph(attributePaths = {"appQuotas", "appQuotas.app"})
    Optional<Plan> findWithAppQuotasById(Long id);
}
