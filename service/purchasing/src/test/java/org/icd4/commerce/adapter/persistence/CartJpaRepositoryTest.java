package org.icd4.commerce.adapter.persistence;

import lombok.extern.slf4j.Slf4j;
import org.icd4.commerce.adapter.persistence.entity.CartItemJpaEntity;
import org.icd4.commerce.adapter.persistence.entity.CartJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CartJpaRepository 통합 테스트.
 * 
 * <p>실제 SQL 쿼리가 어떻게 실행되는지 확인하기 위한 테스트입니다.
 * application-test.yml에서 SQL 로깅을 활성화하여 쿼리를 확인할 수 있습니다.</p>
 */
@Slf4j
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("CartJpaRepository 쿼리 동작 테스트")
@Disabled
class CartJpaRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private CartJpaRepository cartJpaRepository;
    
    private String customerId1;
    private String customerId2;
    private CartJpaEntity activeCart;
    private CartJpaEntity convertedCart;
    
    @BeforeEach
    void setUp() {
        customerId1 = UUID.randomUUID().toString();
        customerId2 = UUID.randomUUID().toString();
        
        // 테스트 데이터 설정
        activeCart = createCart(customerId1, false);
        convertedCart = createCart(customerId1, true);
        
        // 영속성 컨텍스트 초기화
        entityManager.flush();
        entityManager.clear();
        
        log.info("===== 테스트 데이터 준비 완료 =====");
        
        // @BeforeEach에서 실행되는 쿼리: 6개
        // 1. INSERT cart (activeCart)
        // 2. INSERT cart_item1 (activeCart의 첫 번째 아이템)
        // 3. INSERT cart_item2 (activeCart의 두 번째 아이템)
        // 4. INSERT cart (convertedCart) 
        // 5. INSERT cart_item1 (convertedCart의 첫 번째 아이템)
        // 6. INSERT cart_item2 (convertedCart의 두 번째 아이템)
    }
    
    @Test
    @DisplayName("findActiveCartByCustomerId - 고객의 활성 장바구니 조회 쿼리 확인")
    void testFindActiveCartByCustomerId() {
        log.info("===== findActiveCartByCustomerId 쿼리 실행 시작 =====");
        
        // when
        Optional<CartJpaEntity> result = cartJpaRepository.findActiveCartByCustomerId(customerId1);
        
        // then
        assertThat(result).isPresent();
        assertThat(result.get().getCustomerId()).isEqualTo(customerId1);
        assertThat(result.get().isConverted()).isFalse();
        assertThat(result.get().getItems()).hasSize(2); // FETCH JOIN으로 items도 함께 로드
        
        log.info("===== findActiveCartByCustomerId 쿼리 실행 완료 =====");
        log.info("결과: 장바구니 ID = {}, 아이템 수 = {}", 
            result.get().getId(), result.get().getItems().size());
        
        // 실행된 쿼리: 1개 (FETCH JOIN으로 한 번의 쿼리로 cart와 items 모두 조회)
    }
    
    @Test
    @DisplayName("findByCustomerId - 고객의 모든 장바구니 조회 쿼리 확인")
    void testFindByCustomerId() {
        log.info("===== findByCustomerId 쿼리 실행 시작 =====");
        
        // when
        List<CartJpaEntity> results = cartJpaRepository.findByCustomerId(customerId1);
        
        // then
        assertThat(results).hasSize(2);
        assertThat(results).extracting(CartJpaEntity::getCustomerId)
            .containsOnly(customerId1);
        
        // 최신순 정렬 확인
        assertThat(results.get(0).getCreatedAt())
            .isAfterOrEqualTo(results.get(1).getCreatedAt());
        
        log.info("===== findByCustomerId 쿼리 실행 완료 =====");
        log.info("결과: 총 {} 개의 장바구니 조회됨", results.size());
        
        // 실행된 쿼리: 1개 (FETCH JOIN으로 한 번의 쿼리로 모든 cart와 items 조회)
    }
    
    @Test
    @DisplayName("findExpiredCarts - 만료된 장바구니 조회 쿼리 확인")
    void testFindExpiredCarts() {
        log.info("===== findExpiredCarts 쿼리 실행 시작 =====");
        
        // given
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(1);
        
        // when
        List<CartJpaEntity> results = cartJpaRepository.findExpiredCarts(expiryDate);
        
        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).isConverted()).isFalse();
        assertThat(results.get(0).getLastModifiedAt()).isBefore(expiryDate);
        
        log.info("===== findExpiredCarts 쿼리 실행 완료 =====");
        log.info("만료 기준: {}, 조회된 장바구니 수: {}", expiryDate, results.size());
        
        // 실행된 쿼리: 1개 (만료된 cart만 조회, items는 LAZY 로딩)
    }
    
    @Test
    @DisplayName("countActiveCarts - 활성 장바구니 수 카운트 쿼리 확인")
    void testCountActiveCarts() {
        log.info("===== countActiveCarts 쿼리 실행 시작 =====");
        
        // given
        createCart(customerId2, false); // 추가 활성 장바구니
        entityManager.flush();
        entityManager.clear();
        
        // when
        long count = cartJpaRepository.countActiveCarts();
        
        // then
        assertThat(count).isEqualTo(2); // activeCart + 새로 생성한 것
        
        log.info("===== countActiveCarts 쿼리 실행 완료 =====");
        log.info("활성 장바구니 총 개수: {}", count);
        
        // 실행된 쿼리: 1개 (COUNT 쿼리만 실행)
    }
    
    @Test
    @DisplayName("countConvertedCartsBetween - 특정 기간 전환된 장바구니 수 카운트 쿼리 확인")
    void testCountConvertedCartsBetween() {
        log.info("===== countConvertedCartsBetween 쿼리 실행 시작 =====");
        
        // given
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        
        // when
        long count = cartJpaRepository.countConvertedCartsBetween(startDate, endDate);
        
        // then
        assertThat(count).isEqualTo(1); // convertedCart만 해당
        
        log.info("===== countConvertedCartsBetween 쿼리 실행 완료 =====");
        log.info("기간: {} ~ {}, 전환된 장바구니 수: {}", startDate, endDate, count);
        
        // 실행된 쿼리: 1개 (COUNT 쿼리만 실행)
    }
    
    @Test
    @DisplayName("findByIdWithItems - N+1 문제 방지를 위한 FETCH JOIN 쿼리 확인")
    void testFindByIdWithItems() {
        log.info("===== findByIdWithItems 쿼리 실행 시작 =====");
        
        // when
        Optional<CartJpaEntity> result = cartJpaRepository.findByIdWithItems(activeCart.getId());
        
        // then
        assertThat(result).isPresent();
        
        // 영속성 컨텍스트를 비워서 지연 로딩 확인
        entityManager.clear();
        
        // items가 이미 로드되어 있어야 함 (FETCH JOIN 덕분에)
        CartJpaEntity cart = result.get();
        assertThat(cart.getItems()).hasSize(2);
        
        log.info("===== findByIdWithItems 쿼리 실행 완료 =====");
        log.info("FETCH JOIN으로 한 번의 쿼리로 장바구니와 아이템 모두 조회 완료");
        
        // 실행된 쿼리: 1개 (FETCH JOIN으로 N+1 문제 방지)
    }
    
    @Test
    @DisplayName("save - 장바구니 저장 쿼리 확인")
    void testSave() {
        log.info("===== save 쿼리 실행 시작 =====");
        
        // given
        String cartId = UUID.randomUUID().toString();
        String customerId = UUID.randomUUID().toString();
        CartJpaEntity newCart = new CartJpaEntity(cartId, customerId);
        
        // when
        CartJpaEntity savedCart = cartJpaRepository.save(newCart);
        entityManager.flush(); // INSERT 쿼리 즉시 실행
        
        // then
        assertThat(savedCart).isNotNull();
        assertThat(savedCart.getId()).isEqualTo(cartId);
        assertThat(savedCart.getCustomerId()).isEqualTo(customerId);
        
        log.info("===== save 쿼리 실행 완료 =====");
        log.info("저장된 장바구니 ID: {}", savedCart.getId());
        
        // 실행된 쿼리: 2개
        // 1. SELECT (merge 확인용) - 기존 엔티티 존재 여부 확인
        // 2. INSERT - 새 cart 저장
    }
    
    @Test
    @DisplayName("existsById - 존재 여부 확인 쿼리 확인")
    void testExistsById() {
        log.info("===== existsById 쿼리 실행 시작 =====");
        
        // when
        boolean exists = cartJpaRepository.existsById(activeCart.getId());
        boolean notExists = cartJpaRepository.existsById("non-existent-id");
        
        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
        
        log.info("===== existsById 쿼리 실행 완료 =====");
        log.info("COUNT 쿼리로 효율적으로 존재 여부만 확인");
        
        // 실행된 쿼리: 2개 (각각 COUNT 쿼리)
        // 1. COUNT(*) WHERE id = activeCart.getId()
        // 2. COUNT(*) WHERE id = 'non-existent-id'
    }
    
    @Test
    @DisplayName("deleteById - 삭제 쿼리 확인")
    void testDeleteById() {
        log.info("===== deleteById 쿼리 실행 시작 =====");
        
        // given
        String cartIdToDelete = activeCart.getId();
        
        // when
        cartJpaRepository.deleteById(cartIdToDelete);
        entityManager.flush(); // DELETE 쿼리 즉시 실행
        
        // then
        Optional<CartJpaEntity> deleted = cartJpaRepository.findById(cartIdToDelete);
        assertThat(deleted).isEmpty();
        
        log.info("===== deleteById 쿼리 실행 완료 =====");
        log.info("삭제된 장바구니 ID: {}", cartIdToDelete);
        
        // 실행된 쿼리: 5개
        // 1. SELECT cart - 삭제할 엔티티 조회
        // 2. SELECT items - 연관된 items 조회 (LAZY 로딩)
        // 3. DELETE item1 - 첫 번째 아이템 삭제
        // 4. DELETE item2 - 두 번째 아이템 삭제
        // 5. DELETE cart - 장바구니 삭제
        // 6. SELECT cart - findById로 삭제 확인
    }
    
    /**
     * 테스트용 장바구니 생성 헬퍼 메서드
     */
    private CartJpaEntity createCart(String customerId, boolean isConverted) {
        // 간단한 생성자를 사용하여 Cart 생성
        CartJpaEntity cart = new CartJpaEntity(
            UUID.randomUUID().toString(),
            customerId
        );
        cart.setConverted(isConverted);
        
        // 장바구니 아이템 추가
        for (int i = 1; i <= 2; i++) {
            CartItemJpaEntity item = new CartItemJpaEntity(
                UUID.randomUUID().toString(),
                "PRODUCT-" + i,
                "{\"size\": \"M\", \"color\": \"blue\"}",
                i
            );
            cart.addItem(item);  // addItem 메서드 사용으로 양방향 관계 설정
        }
        
        return entityManager.persistAndFlush(cart);
    }
}