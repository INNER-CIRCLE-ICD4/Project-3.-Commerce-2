package org.icd4.commerce.domain.cart;

import org.icd4.commerce.domain.cart.exception.InvalidQuantityException;
import org.icd4.commerce.domain.cart.exception.RequiredOptionMissingException;
import org.icd4.commerce.support.TestTimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CartItemTest {
    
    private CartItemId cartItemId;
    private ProductId productId;
    private ProductOptions options;
    private TimeProvider timeProvider;
    
    @BeforeEach
    void setUp() {
        cartItemId = CartItemId.generate();
        productId = ProductId.of("TEST-001");
        
        Map<String, String> optionMap = new HashMap<>();
        optionMap.put("size", "L");
        optionMap.put("color", "Red");
        options = ProductOptions.of(optionMap);
        TestTimeProvider testTimeProvider = new TestTimeProvider(LocalDateTime.of(2024, 1, 1, 10, 0));
        testTimeProvider.enableAutoAdvance();
        timeProvider = testTimeProvider;
    }
    
    @Test
    @DisplayName("CartItem을 생성할 수 있다")
    void createCartItem() {
        // when
        CartItem cartItem = new CartItem(cartItemId, productId, options, 5, timeProvider);
        
        // then
        assertThat(cartItem.getId()).isEqualTo(cartItemId);
        assertThat(cartItem.getProductId()).isEqualTo(productId);
        assertThat(cartItem.getOptions()).isEqualTo(options);
        assertThat(cartItem.getQuantity()).isEqualTo(5);
        assertThat(cartItem.isAvailable()).isTrue();
        assertThat(cartItem.getAddedAt()).isNotNull();
        assertThat(cartItem.getLastModifiedAt()).isNotNull();
    }
    
    @ParameterizedTest
    @ValueSource(ints = {0, 100, -1, 200})
    @DisplayName("수량이 1-99 범위를 벗어나면 예외가 발생한다")
    void throwExceptionWhenQuantityOutOfRange(int quantity) {
        // when & then
        assertThatThrownBy(() -> new CartItem(cartItemId, productId, options, quantity, timeProvider))
            .isInstanceOf(InvalidQuantityException.class)
            .hasMessageContaining("Quantity must be between 1 and 99");
    }
    
    @Test
    @DisplayName("수량을 업데이트할 수 있다")
    void updateQuantity() {
        // given
        CartItem cartItem = new CartItem(cartItemId, productId, options, 5, timeProvider);
        
        // when
        cartItem.updateQuantity(10);
        
        // then
        assertThat(cartItem.getQuantity()).isEqualTo(10);
        assertThat(cartItem.getLastModifiedAt()).isAfter(cartItem.getAddedAt());
    }
    
    @Test
    @DisplayName("수량 업데이트 시 범위를 벗어나면 예외가 발생한다")
    void throwExceptionWhenUpdateQuantityOutOfRange() {
        // given
        CartItem cartItem = new CartItem(cartItemId, productId, options, 5, timeProvider);
        
        // when & then
        assertThatThrownBy(() -> cartItem.updateQuantity(0))
            .isInstanceOf(InvalidQuantityException.class);
            
        assertThatThrownBy(() -> cartItem.updateQuantity(100))
            .isInstanceOf(InvalidQuantityException.class);
    }
    
    @Test
    @DisplayName("수량을 증가시킬 수 있다")
    void increaseQuantity() {
        // given
        CartItem cartItem = new CartItem(cartItemId, productId, options, 5, timeProvider);
        
        // when
        cartItem.increaseQuantity(3);
        
        // then
        assertThat(cartItem.getQuantity()).isEqualTo(8);
    }
    
    @Test
    @DisplayName("수량 증가 시 최대값을 초과하면 예외가 발생한다")
    void throwExceptionWhenIncreaseQuantityExceedsMax() {
        // given
        CartItem cartItem = new CartItem(cartItemId, productId, options, 95, timeProvider);
        
        // when & then
        assertThatThrownBy(() -> cartItem.increaseQuantity(5))
            .isInstanceOf(InvalidQuantityException.class)
            .hasMessageContaining("Quantity must be between 1 and 99");
    }
    
    @Test
    @DisplayName("동일한 상품인지 판단할 수 있다 - 같은 상품ID와 옵션")
    void isSameProductWithSameOptions() {
        // given
        CartItem cartItem = new CartItem(cartItemId, productId, options, 5, timeProvider);
        
        Map<String, String> sameOptionMap = new HashMap<>();
        sameOptionMap.put("size", "L");
        sameOptionMap.put("color", "Red");
        ProductOptions sameOptions = ProductOptions.of(sameOptionMap);
        
        // when
        boolean result = cartItem.isSameProduct(productId, sameOptions);
        
        // then
        assertThat(result).isTrue();
    }
    
    @Test
    @DisplayName("동일한 상품인지 판단할 수 있다 - 다른 옵션")
    void isSameProductWithDifferentOptions() {
        // given
        CartItem cartItem = new CartItem(cartItemId, productId, options, 5, timeProvider);
        
        Map<String, String> differentOptionMap = new HashMap<>();
        differentOptionMap.put("size", "XL");
        differentOptionMap.put("color", "Red");
        ProductOptions differentOptions = ProductOptions.of(differentOptionMap);
        
        // when
        boolean result = cartItem.isSameProduct(productId, differentOptions);
        
        // then
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("동일한 상품인지 판단할 수 있다 - 다른 상품ID")
    void isSameProductWithDifferentProductId() {
        // given
        CartItem cartItem = new CartItem(cartItemId, productId, options, 5, timeProvider);
        ProductId differentProductId = ProductId.of("TEST-002");
        
        // when
        boolean result = cartItem.isSameProduct(differentProductId, options);
        
        // then
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("구매 불가 상태로 마킹할 수 있다")
    void markAsUnavailable() {
        // given
        CartItem cartItem = new CartItem(cartItemId, productId, options, 5, timeProvider);
        String reason = "Out of stock";
        
        // when
        cartItem.markAsUnavailable(reason);
        
        // then
        assertThat(cartItem.isAvailable()).isFalse();
        assertThat(cartItem.getUnavailableReason()).isEqualTo(reason);
    }
    
    @Test
    @DisplayName("구매 가능 상태로 마킹할 수 있다")
    void markAsAvailable() {
        // given
        CartItem cartItem = new CartItem(cartItemId, productId, options, 5, timeProvider);
        cartItem.markAsUnavailable("Out of stock");
        
        // when
        cartItem.markAsAvailable();
        
        // then
        assertThat(cartItem.isAvailable()).isTrue();
        assertThat(cartItem.getUnavailableReason()).isNull();
    }
    
    @Test
    @DisplayName("필수 옵션이 있으면 검증을 통과한다")
    void validateRequiredOptionsWithAllRequired() {
        // given
        CartItem cartItem = new CartItem(cartItemId, productId, options, 5, timeProvider);
        
        Map<String, Boolean> requiredOptions = new HashMap<>();
        requiredOptions.put("size", true);
        requiredOptions.put("color", true);
        requiredOptions.put("material", false);
        
        // when & then - should not throw exception
        cartItem.validateRequiredOptions(requiredOptions);
    }
    
    @Test
    @DisplayName("필수 옵션이 누락되면 예외가 발생한다")
    void throwExceptionWhenRequiredOptionMissing() {
        // given
        CartItem cartItem = new CartItem(cartItemId, productId, options, 5, timeProvider);
        
        Map<String, Boolean> requiredOptions = new HashMap<>();
        requiredOptions.put("size", true);
        requiredOptions.put("color", true);
        requiredOptions.put("material", true); // This is missing in cartItem options
        
        // when & then
        assertThatThrownBy(() -> cartItem.validateRequiredOptions(requiredOptions))
            .isInstanceOf(RequiredOptionMissingException.class)
            .hasMessageContaining("Required option 'material' is missing");
    }
    
    @Test
    @DisplayName("CartItem의 equals와 hashCode는 ID 기반으로 동작한다")
    void equalsAndHashCode() {
        // given
        CartItem cartItem1 = new CartItem(cartItemId, productId, options, 5, timeProvider);
        CartItem cartItem2 = new CartItem(cartItemId, ProductId.of("TEST-001"), ProductOptions.empty(), 10, timeProvider);
        CartItem cartItem3 = new CartItem(CartItemId.generate(), productId, options, 5, timeProvider);
        
        // then
        assertThat(cartItem1).isEqualTo(cartItem2); // Same ID
        assertThat(cartItem1).isNotEqualTo(cartItem3); // Different ID
        assertThat(cartItem1.hashCode()).isEqualTo(cartItem2.hashCode());
        assertThat(cartItem1.hashCode()).isNotEqualTo(cartItem3.hashCode());
    }
}