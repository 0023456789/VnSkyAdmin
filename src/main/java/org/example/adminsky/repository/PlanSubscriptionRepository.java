package org.example.adminsky.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.example.adminsky.entity.PlanSubscription;
import org.example.adminsky.enums.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanSubscriptionRepository
        extends JpaRepository<PlanSubscription, Long>, JpaSpecificationExecutor<PlanSubscription> {

    @EntityGraph(attributePaths = {"plan", "promoCode", "bonuses"})
    Optional<PlanSubscription> findByMsisdnAndIdempotencyKey(String msisdn, String key);

    boolean existsByMsisdn(String msisdn);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update PlanSubscription s set s.status = :expired, s.updatedAt = :now "
            + "where s.status = :active and s.expiresAt is not null and s.expiresAt <= :now")
    int expireActiveSubscriptions(
            @Param("now") Instant now,
            @Param("active") SubscriptionStatus active,
            @Param("expired") SubscriptionStatus expired);

    long countByPromoCodeIdAndMsisdn(Long promoId, String msisdn);

    boolean existsByPlanId(Long planId);

    boolean existsByPromoCodeId(Long promoId);

    @Override
    @EntityGraph(attributePaths = {"plan", "promoCode"})
    Page<PlanSubscription> findAll(Specification<PlanSubscription> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"plan", "promoCode", "bonuses"})
    Optional<PlanSubscription> findWithDetailById(Long id);

    /** Lấy mức redeem cao nhất theo MSISDN, tính cả subscription đã hủy. */
    @Query("select count(s) from PlanSubscription s "
            + "where s.promoCode.id = :promoId "
            + "group by s.msisdn order by count(s) desc")
    List<Long> findUsesPerMsisdnDesc(
            @Param("promoId") Long promoId,
            Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update PlanSubscription s set s.promoSingleUse = :flag, s.updatedAt = :now "
            + "where s.promoCode.id = :promoId")
    int updatePromoSingleUse(
            @Param("promoId") Long promoId, @Param("flag") boolean flag, @Param("now") Instant now);

    /** Khóa giao dịch theo MSISDN để tuần tự hóa đăng ký; ép kết quả void sang varchar cho JDBC. */
    @Query(value = "select cast(pg_advisory_xact_lock(hashtextextended(:msisdn, 0)) as varchar)", nativeQuery = true)
    String lockMsisdn(@Param("msisdn") String msisdn);
}
