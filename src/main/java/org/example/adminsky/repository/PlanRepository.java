package org.example.adminsky.repository;

import org.example.adminsky.entity.Plan;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import java.util.Optional;
import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long>, JpaSpecificationExecutor<Plan> {
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Long id);
    // Serializes plan edits with any registration holding a PESSIMISTIC_READ lock.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Plan p where p.id = :id")
    Optional<Plan> findByIdForUpdate(@Param("id") Long id);
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select p from Plan p where p.id = :id")
    Optional<Plan> findByIdForShare(@Param("id") Long id);
    // Native update keeps the audit timestamp correct because JPA callbacks do not run for bulk SQL.
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update plan set is_active = :active, updated_at = now() where id = :id", nativeQuery = true)
    int updateStatus(@Param("id") Long id, @Param("active") boolean active);
    @Query(value = "select exists(select 1 from plan_subscription where plan_id = :id)", nativeQuery = true)
    boolean existsSubscription(@Param("id") Long id);
    @Query(value = "select to_regclass('plan_subscription') is not null", nativeQuery = true)
    boolean subscriptionTableExists();
    @EntityGraph(attributePaths = "bonuses")
    @Query("select distinct p from Plan p where p.id = :id")
    Optional<Plan> findDetailWithBonuses(@Param("id") Long id);
    @EntityGraph(attributePaths = {"appQuotas", "appQuotas.app"})
    @Query("select distinct p from Plan p where p.id = :id")
    Optional<Plan> findDetailWithAppQuotas(@Param("id") Long id);
    @Query("select q.plan.id, count(q) from PlanAppQuota q where q.plan.id in :ids group by q.plan.id")
    List<Object[]> countQuotas(@Param("ids") List<Long> ids);
    @Query("select distinct b.plan.id from PlanFirstCycleBonus b where b.plan.id in :ids")
    List<Long> plansWithBonuses(@Param("ids") List<Long> ids);
}
