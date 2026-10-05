package org.example.adminsky.repository;

import java.util.Collection;
import java.util.List;
import org.example.adminsky.entity.PlanFirstCycleBonus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanFirstCycleBonusRepository extends JpaRepository<PlanFirstCycleBonus, Long> {

    /** Which plans of this page have at least one bonus. Skip the call when the page is empty. */
    @Query("select distinct b.plan.id from PlanFirstCycleBonus b where b.plan.id in :planIds")
    List<Long> findPlanIdsWithBonus(@Param("planIds") Collection<Long> planIds);
}
