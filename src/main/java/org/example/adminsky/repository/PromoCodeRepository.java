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

    @EntityGraph(attributePaths = "plans")
    Optional<PromoCode> findWithPlansById(Long id);
}
