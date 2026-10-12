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

    /** Một dòng kết quả của {@link #countByPlanIds}; bí danh JPQL phải khớp với các getter này. */
    interface PlanCountView {
        Long getPlanId();

        Long getCnt();
    }

    /** Mã các gói được gán cho từng ứng dụng; bí danh truy vấn phải khớp với getter của projection. */
    interface AppPlanCodeView {
        Long getAppId();

        String getPlanCode();
    }

    /** Kiểm tra ứng dụng còn được sử dụng trước khi xóa; điều kiện tương ứng với quota.app.id. */
    boolean existsByAppId(Long appId);

    /** Gom số liệu của toàn bộ trang trong một truy vấn; bỏ qua truy vấn này nếu trang rỗng. */
    @Query("select q.plan.id as planId, count(q) as cnt from PlanAppQuota q "
            + "where q.plan.id in :planIds group by q.plan.id")
    List<PlanCountView> countByPlanIds(@Param("planIds") Collection<Long> planIds);

    @Query("select distinct q.app.id as appId, q.plan.code as planCode from PlanAppQuota q "
            + "where q.app.id in :appIds order by q.app.id, q.plan.code")
    List<AppPlanCodeView> findPlanCodesByAppIds(@Param("appIds") Collection<Long> appIds);
}
