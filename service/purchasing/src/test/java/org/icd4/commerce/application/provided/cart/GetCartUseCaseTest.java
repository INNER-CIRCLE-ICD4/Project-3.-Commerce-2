package org.icd4.commerce.application.provided.cart;

import org.icd4.commerce.application.provided.cart.exception.CartNotFoundException;
import org.icd4.commerce.application.provided.cart.usecase.GetCartUseCase;
import org.icd4.commerce.application.required.cart.CartRepositoryPort;
import org.icd4.commerce.domain.cart.*;
import org.icd4.commerce.domain.common.ProductId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * GetCartUseCase 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GetCartUseCase 테스트")
class GetCartUseCaseTest {
    
    @Mock
    private CartRepositoryPort cartRepository;
    
    @Mock
    private ProductPriceProvider productPriceProvider;
    
    @Mock
    private TimeProvider timeProvider;
    
    @InjectMocks
    private GetCartUseCase getCartUseCase;
    
    private CartId cartId;
    private Cart cart;
    private CustomerId customerId;
    private LocalDateTime now;
    
    @BeforeEach
    void setUp() {
        cartId = CartId.of("CART-001");
        customerId = CustomerId.of("CUST-001");
        now = LocalDateTime.of(2024, 1, 1, 12, 0);
        
        when(timeProvider.now()).thenReturn(now);
        
        cart = new Cart(cartId, customerId, timeProvider);
    }
    
    @Test
    @DisplayName("상품이 있는 장바구니 조회 및 총액 계산")
    void execute_CartWithItems_Success() {
        // given
        ProductId productId1 = ProductId.of("PROD-001");
        ProductId productId2 = ProductId.of("PROD-002");
        ProductOptions options1 = ProductOptions.of(Map.of("size", "L"));
        ProductOptions options2 = ProductOptions.empty();
        
        cart.addItem(productId1, 2, options1);
        cart.addItem(productId2, 3, options2);
        
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productPriceProvider.getPrice(productId1)).thenReturn(new BigDecimal("10000"));
        when(productPriceProvider.getPrice(productId2)).thenReturn(new BigDecimal("5000"));
        
        // when
        CartResult result = getCartUseCase.execute(cartId);
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(cartId);
        assertThat(result.customerId()).isEqualTo(customerId);
        assertThat(result.items()).hasSize(2);
        assertThat(result.totalQuantity()).isEqualTo(5);
        assertThat(result.totalAmount()).isEqualTo(new BigDecimal("35000")); // 10000*2 + 5000*3
        assertThat(result.isConverted()).isFalse();
        assertThat(result.createdAt()).isEqualTo(now);
        assertThat(result.lastModifiedAt()).isEqualTo(now);
        
        // 첫 번째 아이템 검증
        CartResult.CartItemResult firstItem = result.items().get(0);
        assertThat(firstItem.productId()).isEqualTo(productId1.value().toString());
        assertThat(firstItem.quantity()).isEqualTo(2);
        assertThat(firstItem.options().options()).containsEntry("size", "L");
        
        // 두 번째 아이템 검증
        CartResult.CartItemResult secondItem = result.items().get(1);
        assertThat(secondItem.productId()).isEqualTo(productId2.value().toString());
        assertThat(secondItem.quantity()).isEqualTo(3);
        assertThat(secondItem.options().options()).isEmpty();
        
        verify(cartRepository).findById(cartId);
        verify(productPriceProvider).getPrice(productId1);
        verify(productPriceProvider).getPrice(productId2);
    }
    
    @Test
    @DisplayName("빈 장바구니 조회")
    void execute_EmptyCart_Success() {
        // given
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        
        // when
        CartResult result = getCartUseCase.execute(cartId);
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(cartId);
        assertThat(result.customerId()).isEqualTo(customerId);
        assertThat(result.items()).isEmpty();
        assertThat(result.totalQuantity()).isEqualTo(0);
        assertThat(result.totalAmount()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.isConverted()).isFalse();
        
        verify(cartRepository).findById(cartId);
        verify(productPriceProvider, never()).getPrice(any());
    }
    
    @Test
    @DisplayName("존재하지 않는 장바구니 조회")
    void execute_CartNotFound() {
        // given
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());
        
        // when & then
        assertThatThrownBy(() -> getCartUseCase.execute(cartId))
            .isInstanceOf(CartNotFoundException.class)
            .hasMessageContaining(cartId.value());
        
        verify(cartRepository).findById(cartId);
        verify(productPriceProvider, never()).getPrice(any());
    }
    
    @Test
    @DisplayName("전환된 장바구니 조회")
    void execute_ConvertedCart_Success() {
        // given
        ProductId productId = ProductId.of("PROD-001");
        cart.addItem(productId, 1, ProductOptions.empty());
        cart.convertToOrder();
        
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productPriceProvider.getPrice(productId)).thenReturn(new BigDecimal("10000"));
        
        // when
        CartResult result = getCartUseCase.execute(cartId);
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.isConverted()).isTrue();
        assertThat(result.items()).hasSize(1);
        assertThat(result.totalAmount()).isEqualTo(new BigDecimal("10000"));
        
        verify(cartRepository).findById(cartId);
        verify(productPriceProvider).getPrice(productId);
    }
    
    @Test
    @DisplayName("다양한 옵션의 동일 상품이 있는 장바구니 조회")
    void execute_SameProductDifferentOptions() {
        // given
        ProductId productId = ProductId.of("PROD-001");
        ProductOptions options1 = ProductOptions.of(Map.of("size", "L", "color", "black"));
        ProductOptions options2 = ProductOptions.of(Map.of("size", "M", "color", "white"));
        
        cart.addItem(productId, 2, options1);
        cart.addItem(productId, 1, options2);
        
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productPriceProvider.getPrice(productId)).thenReturn(new BigDecimal("15000"));
        
        // when
        CartResult result = getCartUseCase.execute(cartId);
        
        // then
        assertThat(result.items()).hasSize(2);
        assertThat(result.totalQuantity()).isEqualTo(3);
        assertThat(result.totalAmount()).isEqualTo(new BigDecimal("45000")); // 15000*3
        
        // 옵션 검증
        assertThat(result.items().get(0).options().options())
            .containsEntry("size", "L")
            .containsEntry("color", "black");
        assertThat(result.items().get(1).options().options())
            .containsEntry("size", "M")
            .containsEntry("color", "white");
        
        verify(cartRepository).findById(cartId);
        // 동일 상품이지만 다른 옵션으로 2개의 아이템이므로 2번 호출
        verify(productPriceProvider, times(2)).getPrice(productId);
    }
    
    @Test
    @DisplayName("가격 제공자가 null을 반환하는 경우")
    void execute_PriceProviderReturnsNull() {
        // given
        ProductId productId = ProductId.of("PROD-001");
        cart.addItem(productId, 1, ProductOptions.empty());
        
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productPriceProvider.getPrice(productId)).thenReturn(null);
        
        // when & then
        assertThatThrownBy(() -> getCartUseCase.execute(cartId))
            .isInstanceOf(NullPointerException.class);
        
        verify(cartRepository).findById(cartId);
        verify(productPriceProvider).getPrice(productId);
    }
}