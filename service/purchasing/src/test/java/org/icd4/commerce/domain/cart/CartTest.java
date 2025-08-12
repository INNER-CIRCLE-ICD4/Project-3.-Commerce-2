package org.icd4.commerce.domain.cart;

import org.icd4.commerce.domain.cart.exception.CartAlreadyConvertedException;
import org.icd4.commerce.domain.cart.exception.CartItemLimitExceededException;
import org.icd4.commerce.domain.cart.exception.InvalidCartStateException;
import org.icd4.commerce.domain.common.ProductId;
import org.icd4.commerce.domain.common.ProductPriceProvider;
import org.icd4.commerce.support.TestTimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class CartTest {
    
    @Mock
    private ProductPriceProvider priceProvider;
    
    private Cart cart;
    private CartId cartId;
    private CustomerId customerId;
    private TimeProvider timeProvider;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cartId = CartId.generate();
        customerId = CustomerId.of("customer-123");
        TestTimeProvider testTimeProvider = new TestTimeProvider(LocalDateTime.of(2024, 1, 1, 10, 0));
        testTimeProvider.enableAutoAdvance();
        timeProvider = testTimeProvider;
        cart = new Cart(cartId, customerId, timeProvider);
    }
    
    @Test
    @DisplayName("장바구니에 새로운 상품을 추가할 수 있다")
    void addNewItem() {
        // given
        ProductId productId = ProductId.of("TEST-001");
        Map<String, String> optionMap = new HashMap<>();
        optionMap.put("size", "L");
        ProductOptions options = ProductOptions.of(optionMap);
        
        // when
        cart.addItem(productId, 2, options);
        
        // then
        assertThat(cart.getItemCount()).isEqualTo(1);
        assertThat(cart.getTotalQuantity()).isEqualTo(2);
        assertThat(cart.getItems().get(0).getProductId()).isEqualTo(productId);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("동일한 상품과 옵션이면 수량이 합산된다")
    void addSameItemWithSameOptions() {
        // given
        ProductId productId = ProductId.of("TEST-001");
        Map<String, String> optionMap = new HashMap<>();
        optionMap.put("size", "L");
        ProductOptions options = ProductOptions.of(optionMap);
        
        // when
        cart.addItem(productId, 2, options);
        cart.addItem(productId, 3, options);
        
        // then
        assertThat(cart.getItemCount()).isEqualTo(1);
        assertThat(cart.getTotalQuantity()).isEqualTo(5);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(5);
    }
    
    @Test
    @DisplayName("동일한 상품이지만 옵션이 다르면 별개의 아이템으로 추가된다")
    void addSameItemWithDifferentOptions() {
        // given
        ProductId productId = ProductId.of("TEST-001");
        Map<String, String> optionMap1 = new HashMap<>();
        optionMap1.put("size", "L");
        ProductOptions options1 = ProductOptions.of(optionMap1);
        
        Map<String, String> optionMap2 = new HashMap<>();
        optionMap2.put("size", "XL");
        ProductOptions options2 = ProductOptions.of(optionMap2);
        
        // when
        cart.addItem(productId, 2, options1);
        cart.addItem(productId, 3, options2);
        
        // then
        assertThat(cart.getItemCount()).isEqualTo(2);
        assertThat(cart.getTotalQuantity()).isEqualTo(5);
    }
    
    @Test
    @DisplayName("50개 상품 종류 제한을 초과하면 예외가 발생한다")
    void throwExceptionWhenExceedItemTypeLimit() {
        // given
        for (int i = 1; i <= 50; i++) {
            cart.addItem(ProductId.of(String.valueOf(i)), 1, ProductOptions.empty());
        }
        
        // when & then
        assertThatThrownBy(() -> cart.addItem(ProductId.of("TEST-001"), 1, ProductOptions.empty()))
            .isInstanceOf(CartItemLimitExceededException.class)
            .hasMessageContaining("Cannot add more than 50 different product types");
    }
    
    @Test
    @DisplayName("장바구니에서 아이템을 제거할 수 있다")
    void removeItem() {
        // given
        ProductId productId = ProductId.of("TEST-001");
        cart.addItem(productId, 2, ProductOptions.empty());
        CartItemId itemId = cart.getItems().get(0).getId();
        
        // when
        cart.removeItem(itemId);
        
        // then
        assertThat(cart.getItemCount()).isEqualTo(0);
    }
    
    @Test
    @DisplayName("존재하지 않는 아이템을 제거하려고 하면 예외가 발생한다")
    void throwExceptionWhenRemoveNonExistentItem() {
        // given
        CartItemId nonExistentId = CartItemId.generate();
        
        // when & then
        assertThatThrownBy(() -> cart.removeItem(nonExistentId))
            .isInstanceOf(InvalidCartStateException.class)
            .hasMessageContaining("Cart item not found");
    }
    
    @Test
    @DisplayName("장바구니 아이템의 수량을 변경할 수 있다")
    void updateItemQuantity() {
        // given
        ProductId productId = ProductId.of("TEST-001");
        cart.addItem(productId, 2, ProductOptions.empty());
        CartItemId itemId = cart.getItems().get(0).getId();
        
        // when
        cart.updateQuantity(itemId, 5);
        
        // then
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(5);
    }
    
    @Test
    @DisplayName("장바구니를 비울 수 있다")
    void clearCart() {
        // given
        cart.addItem(ProductId.of("TEST-001"), 2, ProductOptions.empty());
        cart.addItem(ProductId.of("TEST-001"), 3, ProductOptions.empty());
        
        // when
        cart.clear();
        
        // then
        assertThat(cart.getItemCount()).isEqualTo(0);
        assertThat(cart.getTotalQuantity()).isEqualTo(0);
    }
    
    @Test
    @DisplayName("장바구니 총액을 계산할 수 있다")
    void calculateTotal() {
        // given
        ProductId productId1 = ProductId.of("TEST-001");
        ProductId productId2 = ProductId.of("TEST-002");
        
        cart.addItem(productId1, 2, ProductOptions.empty());
        cart.addItem(productId2, 3, ProductOptions.empty());
        
        when(priceProvider.getPrice(productId1)).thenReturn(new BigDecimal("10000"));
        when(priceProvider.getPrice(productId2)).thenReturn(new BigDecimal("5000"));
        
        // when
        BigDecimal total = cart.calculateTotal(priceProvider);
        
        // then
        assertThat(total).isEqualTo(new BigDecimal("35000"));
    }
    
    @Test
    @DisplayName("다른 장바구니와 병합할 수 있다")
    void mergeWithOtherCart() {
        // given
        Cart otherCart = new Cart(CartId.generate(), CustomerId.of("customer-456"), timeProvider);
        
        ProductId productId1 = ProductId.of("TEST-001");
        ProductId productId2 = ProductId.of("TEST-002");
        
        cart.addItem(productId1, 2, ProductOptions.empty());
        otherCart.addItem(productId1, 3, ProductOptions.empty());
        otherCart.addItem(productId2, 1, ProductOptions.empty());
        
        // when
        cart.merge(otherCart);
        
        // then
        assertThat(cart.getItemCount()).isEqualTo(2);
        assertThat(cart.getTotalQuantity()).isEqualTo(6);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(5);
    }
    
    @Test
    @DisplayName("장바구니를 주문으로 전환할 수 있다")
    void convertToOrder() {
        // given
        cart.addItem(ProductId.of("TEST-001"), 2, ProductOptions.empty());
        
        // when
        cart.convertToOrder();
        
        // then
        assertThat(cart.isConverted()).isTrue();
    }
    
    @Test
    @DisplayName("이미 전환된 장바구니는 다시 전환할 수 없다")
    void cannotConvertAlreadyConvertedCart() {
        // given
        cart.addItem(ProductId.of("TEST-001"), 2, ProductOptions.empty());
        cart.convertToOrder();
        
        // when & then
        assertThatThrownBy(() -> cart.convertToOrder())
            .isInstanceOf(CartAlreadyConvertedException.class)
            .hasMessageContaining("Cannot modify a converted cart");
    }
    
    @Test
    @DisplayName("빈 장바구니는 주문으로 전환할 수 없다")
    void cannotConvertEmptyCart() {
        // when & then
        assertThatThrownBy(() -> cart.convertToOrder())
            .isInstanceOf(InvalidCartStateException.class)
            .hasMessageContaining("Cannot convert an empty cart");
    }
    
    @Test
    @DisplayName("전환된 장바구니는 수정할 수 없다")
    void cannotModifyConvertedCart() {
        // given
        cart.addItem(ProductId.of("TEST-001"), 2, ProductOptions.empty());
        cart.convertToOrder();
        
        // when & then
        assertThatThrownBy(() -> cart.addItem(ProductId.of("TEST-001"), 1, ProductOptions.empty()))
            .isInstanceOf(CartAlreadyConvertedException.class)
            .hasMessageContaining("Cannot modify a converted cart");
            
        assertThatThrownBy(() -> cart.clear())
            .isInstanceOf(CartAlreadyConvertedException.class);
    }
    
    @Test
    @DisplayName("실패한 주문으로부터 장바구니를 복원할 수 있다")
    void restoreFromFailedOrder() {
        // given
        CartId newCartId = CartId.generate();
        CustomerId customerId = CustomerId.of("customer-456");
        
        // 기존 장바구니 아이템 생성
        CartItem item1 = new CartItem(
            CartItemId.generate(),
            ProductId.of("TEST-001"),
            ProductOptions.empty(),
            2,
            timeProvider
        );
        CartItem item2 = new CartItem(
            CartItemId.generate(),
            ProductId.of("TEST-001"),
            ProductOptions.of(Map.of("color", "red")),
            1,
            timeProvider
        );
        
        // when
        Cart restoredCart = Cart.restoreFromFailedOrder(
            newCartId,
            customerId,
            List.of(item1, item2),
            timeProvider
        );
        
        // then
        assertThat(restoredCart).isNotNull();
        assertThat(restoredCart.getId()).isEqualTo(newCartId);
        assertThat(restoredCart.getCustomerId()).isEqualTo(customerId);
        assertThat(restoredCart.isConverted()).isFalse();
        assertThat(restoredCart.getItems()).hasSize(2);
        assertThat(restoredCart.getItems().get(0).getProductId()).isEqualTo(ProductId.of("TEST-001"));
        assertThat(restoredCart.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(restoredCart.getItems().get(1).getProductId()).isEqualTo(ProductId.of("TEST-001"));
        assertThat(restoredCart.getItems().get(1).getQuantity()).isEqualTo(1);
    }
    
    @Test
    @DisplayName("restoreFromFailedOrder는 null 매개변수를 허용하지 않는다")
    void restoreFromFailedOrderWithNullParameters() {
        // given
        CartId cartId = CartId.generate();
        CustomerId customerId = CustomerId.of("customer-123");
        List<CartItem> items = List.of();
        
        // when & then
        assertThatThrownBy(() -> Cart.restoreFromFailedOrder(null, customerId, items, timeProvider))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("CartId cannot be null");
            
        assertThatThrownBy(() -> Cart.restoreFromFailedOrder(cartId, null, items, timeProvider))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("CustomerId cannot be null");
            
        assertThatThrownBy(() -> Cart.restoreFromFailedOrder(cartId, customerId, null, timeProvider))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("Items cannot be null");
            
        assertThatThrownBy(() -> Cart.restoreFromFailedOrder(cartId, customerId, items, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("TimeProvider cannot be null");
    }
}