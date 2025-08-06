package org.icd4.commerce.adapter.persistence;

import org.icd4.commerce.application.required.cart.CartRepositoryPort;
import org.icd4.commerce.domain.cart.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * CartRepositoryAdapter 단위 테스트.
 * 
 * <p>CartRepository와의 상호작용 및 도메인 객체 변환을 테스트합니다.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CartRepositoryAdapter 단위 테스트")
class CartRepositoryAdapterTest {
    
    @Mock
    private CartRepository cartRepository;
    
    @Mock
    private TimeProvider timeProvider;
    
    @InjectMocks
    private CartRepositoryAdapter cartRepositoryAdapter;
    
    private CartId cartId;
    private CustomerId customerId;
    private Cart testCart;
    private LocalDateTime now;
    
    @BeforeEach
    void setUp() {
        cartId = CartId.generate();
        customerId = CustomerId.of("CUST-123");
        now = LocalDateTime.now();
        
        when(timeProvider.now()).thenReturn(now);
        
        // 테스트용 Cart 생성
        testCart = new Cart(cartId, customerId, timeProvider);
    }
    
    @Test
    @DisplayName("save - 장바구니 저장 시 CartRepository.save 호출 확인")
    void testSave() {
        // given
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);
        
        // when
        Cart savedCart = cartRepositoryAdapter.save(testCart);
        
        // then
        assertThat(savedCart).isEqualTo(testCart);
        verify(cartRepository, times(1)).save(testCart);
        
        // @Transactional 애노테이션으로 인해 트랜잭션 내에서 실행됨
    }
    
    @Test
    @DisplayName("findById - ID로 장바구니 조회")
    void testFindById() {
        // given
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
        
        // when
        Optional<Cart> found = cartRepositoryAdapter.findById(cartId);
        
        // then
        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(testCart);
        verify(cartRepository, times(1)).findById(cartId);
    }
    
    @Test
    @DisplayName("findById - 존재하지 않는 장바구니 조회 시 빈 Optional 반환")
    void testFindByIdNotFound() {
        // given
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());
        
        // when
        Optional<Cart> found = cartRepositoryAdapter.findById(cartId);
        
        // then
        assertThat(found).isEmpty();
        verify(cartRepository, times(1)).findById(cartId);
    }
    
    @Test
    @DisplayName("findActiveCartByCustomerId - 고객의 활성 장바구니 조회")
    void testFindActiveCartByCustomerId() {
        // given
        when(cartRepository.findActiveCartByCustomerId(customerId))
            .thenReturn(Optional.of(testCart));
        
        // when
        Optional<Cart> found = cartRepositoryAdapter.findActiveCartByCustomerId(customerId);
        
        // then
        assertThat(found).isPresent();
        assertThat(found.get().getCustomerId()).isEqualTo(customerId);
        verify(cartRepository, times(1)).findActiveCartByCustomerId(customerId);
    }
    
    @Test
    @DisplayName("findExpiredCarts - 만료된 장바구니 목록 조회")
    void testFindExpiredCarts() {
        // given
        LocalDateTime expiryDate = now.minusDays(90);
        List<Cart> expiredCarts = Arrays.asList(
            new Cart(CartId.generate(), CustomerId.of("CUST-001"), timeProvider),
            new Cart(CartId.generate(), CustomerId.of("CUST-002"), timeProvider)
        );
        when(cartRepository.findExpiredCarts(expiryDate)).thenReturn(expiredCarts);
        
        // when
        List<Cart> found = cartRepositoryAdapter.findExpiredCarts(expiryDate);
        
        // then
        assertThat(found).hasSize(2);
        assertThat(found).isEqualTo(expiredCarts);
        verify(cartRepository, times(1)).findExpiredCarts(expiryDate);
    }
    
    @Test
    @DisplayName("deleteById - ID로 장바구니 삭제")
    void testDeleteById() {
        // given
        doNothing().when(cartRepository).deleteById(cartId);
        
        // when
        cartRepositoryAdapter.deleteById(cartId);
        
        // then
        verify(cartRepository, times(1)).deleteById(cartId);
        
        // @Transactional 애노테이션으로 인해 트랜잭션 내에서 실행됨
    }
    
    @Test
    @DisplayName("existsById - 장바구니 존재 여부 확인")
    void testExistsById() {
        // given
        when(cartRepository.existsById(cartId)).thenReturn(true);
        
        // when
        boolean exists = cartRepositoryAdapter.existsById(cartId);
        
        // then
        assertThat(exists).isTrue();
        verify(cartRepository, times(1)).existsById(cartId);
    }
    
    @Test
    @DisplayName("existsById - 존재하지 않는 장바구니 확인")
    void testExistsByIdNotFound() {
        // given
        when(cartRepository.existsById(cartId)).thenReturn(false);
        
        // when
        boolean exists = cartRepositoryAdapter.existsById(cartId);
        
        // then
        assertThat(exists).isFalse();
        verify(cartRepository, times(1)).existsById(cartId);
    }
    
    @Test
    @DisplayName("트랜잭션 경계 확인 - readOnly 트랜잭션은 조회 메서드에만 적용")
    void testTransactionalBoundaries() {
        // 조회 메서드들은 @Transactional(readOnly = true)가 클래스 레벨에 적용됨
        
        // given
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(testCart));
        when(cartRepository.existsById(cartId)).thenReturn(true);
        
        // when - 여러 조회 작업이 하나의 읽기 전용 트랜잭션 내에서 실행
        cartRepositoryAdapter.findById(cartId);
        cartRepositoryAdapter.existsById(cartId);
        cartRepositoryAdapter.findActiveCartByCustomerId(customerId);
        
        // then
        verify(cartRepository, times(1)).findById(cartId);
        verify(cartRepository, times(1)).existsById(cartId);
        verify(cartRepository, times(1)).findActiveCartByCustomerId(customerId);
    }
    
    @Test
    @DisplayName("save와 deleteById는 쓰기 트랜잭션에서 실행")
    void testWriteTransactions() {
        // save와 deleteById 메서드는 @Transactional 애노테이션으로 
        // 쓰기 가능한 트랜잭션에서 실행됨
        
        // given
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);
        
        // when
        cartRepositoryAdapter.save(testCart);
        cartRepositoryAdapter.deleteById(cartId);
        
        // then
        verify(cartRepository, times(1)).save(testCart);
        verify(cartRepository, times(1)).deleteById(cartId);
    }
    
    @Test
    @DisplayName("어댑터 패턴 검증 - 도메인 포트 구현")
    void testAdapterPattern() {
        // CartRepositoryAdapter가 CartRepositoryPort 인터페이스를 구현하는지 확인
        assertThat(cartRepositoryAdapter).isInstanceOf(CartRepositoryPort.class);
        
        // 모든 포트 메서드가 CartRepository로 위임되는지 확인
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);
        when(cartRepository.findById(any(CartId.class))).thenReturn(Optional.of(testCart));
        when(cartRepository.findActiveCartByCustomerId(any(CustomerId.class)))
            .thenReturn(Optional.of(testCart));
        when(cartRepository.findExpiredCarts(any(LocalDateTime.class)))
            .thenReturn(Collections.emptyList());
        when(cartRepository.existsById(any(CartId.class))).thenReturn(true);
        
        // 포트의 모든 메서드 호출
        cartRepositoryAdapter.save(testCart);
        cartRepositoryAdapter.findById(cartId);
        cartRepositoryAdapter.findActiveCartByCustomerId(customerId);
        cartRepositoryAdapter.findExpiredCarts(now);
        cartRepositoryAdapter.deleteById(cartId);
        cartRepositoryAdapter.existsById(cartId);
        
        // 각 메서드가 정확히 한 번씩 CartRepository로 위임되었는지 확인
        verify(cartRepository, times(1)).save(testCart);
        verify(cartRepository, times(1)).findById(cartId);
        verify(cartRepository, times(1)).findActiveCartByCustomerId(customerId);
        verify(cartRepository, times(1)).findExpiredCarts(now);
        verify(cartRepository, times(1)).deleteById(cartId);
        verify(cartRepository, times(1)).existsById(cartId);
    }
}