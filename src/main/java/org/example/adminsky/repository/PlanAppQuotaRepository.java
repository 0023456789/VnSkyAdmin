package org.example.adminsky.repository;

import java.util.Collection;
import java.util.List;
import org.example.adminsky.entity.PlanAppQuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanAppQuotaRepository extends JpaRepository<PlanAppQuota, Long> {

    /** Row of {@link #countByPlanIds}; the JPQL aliases must match these getters. */
    interface PlanCountView {
        Long getPlanId();

        Long getCnt();
    }

    /** Delete guard for App ("app is in use"). Resolves to quota.app.id. */
    boolean existsByAppId(Long appId);

    /** One grouped query for a whole page of plans. Skip the call when the page is empty. */
    @Query("select q.plan.id as planId, count(q) as cnt from PlanAppQuota q "
            + "where q.plan.id in :planIds group by q.plan.id")
    List<PlanCountView> countByPlanIds(@Param("planIds") Collection<Long> planIds);
}
