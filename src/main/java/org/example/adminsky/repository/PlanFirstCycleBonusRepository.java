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

    /** Tìm các gói trong trang có ít nhất một khoản thưởng; bỏ qua truy vấn nếu trang rỗng. */
    @Query("select distinct b.plan.id from PlanFirstCycleBonus b where b.plan.id in :planIds")
    List<Long> findPlanIdsWithBonus(@Param("planIds") Collection<Long> planIds);
}
