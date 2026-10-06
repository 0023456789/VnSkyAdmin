package org.example.adminsky.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.example.adminsky.entity.PromoCode;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long>, JpaSpecificationExecutor<PromoCode> {
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Long id);
    Optional<PromoCode> findByCode(String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PromoCode p where p.id = :id")
    Optional<PromoCode> findForUpdateById(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PromoCode p where p.code = :code")
    Optional<PromoCode> findForUpdateByCode(@Param("code") String code);

    @Modifying(flushAutomatically = true)
    @Query(value = "UPDATE promo_code SET used_count = used_count + 1, updated_at = :now "
            + "WHERE id = :id AND is_active = TRUE "
            + "AND (usage_limit IS NULL OR used_count < usage_limit) "
            + "AND (valid_from IS NULL OR valid_from <= :now) "
            + "AND (valid_to IS NULL OR valid_to >= :now)", nativeQuery = true)
    int consume(@Param("id") Long id, @Param("now") java.time.Instant now);

    @EntityGraph(attributePaths = "plans")
    Optional<PromoCode> findWithPlansById(Long id);
}
