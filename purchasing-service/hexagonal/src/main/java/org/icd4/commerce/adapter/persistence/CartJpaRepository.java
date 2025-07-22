package org.icd4.commerce.adapter.persistence;

import org.icd4.commerce.adapter.persistence.entity.CartJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA 레포지토리 인터페이스.
 * 
 * <p>CartJpaEntity에 대한 데이터베이스 접근을 담당합니다.</p>
 * 
 * @author Jooeun
 * @since 1.0
 */
@Repository
public interface CartJpaRepository extends JpaRepository<CartJpaEntity, String> {
    
    /**
     * 고객 ID로 전환되지 않은 장바구니를 조회합니다.
     * 
     * @param customerId 고객 ID
     * @return 장바구니 엔티티 (Optional)
     */
    @Query("SELECT c FROM CartJpaEntity c LEFT JOIN FETCH c.items WHERE c.customerId = :customerId AND c.isConverted = false")
    Optional<CartJpaEntity> findActiveCartByCustomerId(@Param("customerId") String customerId);
    
    /**
     * 고객 ID로 모든 장바구니를 조회합니다.
     * 
     * @param customerId 고객 ID
     * @return 장바구니 목록
     */
    @Query("SELECT c FROM CartJpaEntity c LEFT JOIN FETCH c.items WHERE c.customerId = :customerId ORDER BY c.createdAt DESC")
    List<CartJpaEntity> findByCustomerId(@Param("customerId") String customerId);
    
    /**
     * 특정 날짜 이전에 마지막으로 수정된 전환되지 않은 장바구니들을 조회합니다.
     * 
     * @param date 기준 날짜
     * @return 만료된 장바구니 목록
     */
    @Query("SELECT c FROM CartJpaEntity c WHERE c.isConverted = false AND c.lastModifiedAt < :date")
    List<CartJpaEntity> findExpiredCarts(@Param("date") LocalDateTime date);
    
    /**
     * 전환되지 않은 장바구니의 수를 조회합니다.
     * 
     * @return 전환되지 않은 장바구니 수
     */
    @Query("SELECT COUNT(c) FROM CartJpaEntity c WHERE c.isConverted = false")
    long countActiveCarts();
    
    /**
     * 특정 기간 동안 전환된 장바구니 수를 조회합니다.
     * 
     * @param startDate 시작 일시
     * @param endDate 종료 일시
     * @return 전환된 장바구니 수
     */
    @Query("SELECT COUNT(c) FROM CartJpaEntity c WHERE c.isConverted = true " +
           "AND c.lastModifiedAt BETWEEN :startDate AND :endDate")
    long countConvertedCartsBetween(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);
    
    /**
     * 장바구니와 관련된 모든 아이템을 함께 조회합니다.
     * N+1 문제를 방지하기 위한 fetch join 사용.
     * 
     * @param cartId 장바구니 ID
     * @return 아이템과 함께 로드된 장바구니
     */
    @Query("SELECT c FROM CartJpaEntity c LEFT JOIN FETCH c.items WHERE c.id = :cartId")
    Optional<CartJpaEntity> findByIdWithItems(@Param("cartId") String cartId);
}