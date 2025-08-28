package org.icd4.commerce.application.provided.cart;

import org.icd4.commerce.application.provided.cart.command.RemoveItemFromCartCommand;
import org.icd4.commerce.application.provided.cart.exception.CartNotFoundException;
import org.icd4.commerce.application.provided.cart.usecase.RemoveItemFromCartUseCase;
import org.icd4.commerce.application.required.cart.CartRepositoryPort;
import org.icd4.commerce.domain.cart.*;
import org.icd4.commerce.domain.cart.exception.CartAlreadyConvertedException;
import org.icd4.commerce.domain.cart.exception.InvalidCartStateException;
import org.icd4.commerce.domain.common.ProductId;
import org.icd4.commerce.domain.common.StockKeepingUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * RemoveItemFromCartUseCase 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RemoveItemFromCartUseCase 테스트")
class RemoveItemFromCartUseCaseTest {

    @Mock
    private CartRepositoryPort cartRepository;

    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private RemoveItemFromCartUseCase removeItemFromCartUseCase;

    private CartId cartId;
    private Cart cart;
    private CustomerId customerId;

    @BeforeEach
    void setUp() {
        cartId = CartId.of("CART-001");
        customerId = CustomerId.of("CUST-001");

        when(timeProvider.now()).thenReturn(LocalDateTime.of(2024, 1, 1, 12, 0));

        cart = new Cart(cartId, customerId, timeProvider);
    }

    @Test
    @DisplayName("장바구니에서 상품 제거 성공")
    void execute_RemoveItem_Success() {
        // given
        ProductId productId1 = ProductId.of("PROD-001");
        ProductId productId2 = ProductId.of("PROD-002");

        StockKeepingUnit sku1 = StockKeepingUnit.of("SKU-001");
        StockKeepingUnit sku2 = StockKeepingUnit.of("SKU-002");

        cart.addItem(productId1, sku1, 2, ProductOptions.of(Map.of("size", "L")));
        cart.addItem(productId2, sku2, 3, ProductOptions.empty());

        // 첫 번째 아이템의 ID 가져오기
        CartItemId itemIdToRemove = cart.getItems().get(0).getId();

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        RemoveItemFromCartCommand command = new RemoveItemFromCartCommand(cartId, itemIdToRemove);

        // when
        removeItemFromCartUseCase.execute(command);

        // then
        verify(cartRepository).findById(cartId);
        verify(cartRepository).save(argThat(savedCart ->
                savedCart.getItemCount() == 1 &&
                        savedCart.getTotalQuantity() == 3
        ));

        // 남은 아이템 확인
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getProductId()).isEqualTo(productId2);
    }

    @Test
    @DisplayName("마지막 상품 제거 후 빈 장바구니 상태")
    void execute_RemoveLastItem_EmptyCart() {
        // given
        ProductId productId = ProductId.of("PROD-001");
        StockKeepingUnit sku = StockKeepingUnit.of("SKU-001");
        cart.addItem(productId, sku, 1, ProductOptions.empty());

        CartItemId itemIdToRemove = cart.getItems().get(0).getId();

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        RemoveItemFromCartCommand command = new RemoveItemFromCartCommand(cartId, itemIdToRemove);

        // when
        removeItemFromCartUseCase.execute(command);

        // then
        verify(cartRepository).save(argThat(savedCart ->
                savedCart.getItemCount() == 0 &&
                        savedCart.getTotalQuantity() == 0
        ));

        assertThat(cart.getItems()).isEmpty();
    }

    @Test
    @DisplayName("장바구니를 찾을 수 없는 경우")
    void execute_CartNotFound() {
        // given
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        CartItemId cartItemId = CartItemId.of("ITEM-001");
        RemoveItemFromCartCommand command = new RemoveItemFromCartCommand(cartId, cartItemId);

        // when & then
        assertThatThrownBy(() -> removeItemFromCartUseCase.execute(command))
                .isInstanceOf(CartNotFoundException.class)
                .hasMessageContaining(cartId.value());

        verify(cartRepository).findById(cartId);
        verify(cartRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 아이템 제거 시도")
    void execute_ItemNotFound() {
        // given
        ProductId productId = ProductId.of("PROD-001");
        StockKeepingUnit sku = StockKeepingUnit.of("SKU-001");
        cart.addItem(productId, sku, 2, ProductOptions.empty());

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        CartItemId nonExistentItemId = CartItemId.of("NON-EXISTENT-ITEM");
        RemoveItemFromCartCommand command = new RemoveItemFromCartCommand(cartId, nonExistentItemId);

        // when & then
        assertThatThrownBy(() -> removeItemFromCartUseCase.execute(command))
                .isInstanceOf(InvalidCartStateException.class);

        verify(cartRepository).findById(cartId);
        verify(cartRepository, never()).save(any());
    }

    @Test
    @DisplayName("이미 전환된 장바구니에서 제거 시도")
    void execute_AlreadyConverted() {
        // given
        ProductId productId = ProductId.of("PROD-001");
        StockKeepingUnit sku = StockKeepingUnit.of("SKU-001");
        cart.addItem(productId, sku,1, ProductOptions.empty());
        CartItemId itemId = cart.getItems().get(0).getId();
        cart.convertToOrder();

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        RemoveItemFromCartCommand command = new RemoveItemFromCartCommand(cartId, itemId);

        // when & then
        assertThatThrownBy(() -> removeItemFromCartUseCase.execute(command))
                .isInstanceOf(CartAlreadyConvertedException.class);

        verify(cartRepository).findById(cartId);
        verify(cartRepository, never()).save(any());
    }

    @Test
    @DisplayName("여러 아이템 중 특정 아이템만 제거")
    void execute_RemoveSpecificItem_Success() {
        // given
        ProductId productId1 = ProductId.of("PROD-001");
        ProductId productId2 = ProductId.of("PROD-002");
        ProductId productId3 = ProductId.of("PROD-003");
        StockKeepingUnit sku1 = StockKeepingUnit.of("SKU-001");
        StockKeepingUnit sku2 = StockKeepingUnit.of("SKU-002");
        StockKeepingUnit sku3 = StockKeepingUnit.of("SKU-003");

        cart.addItem(productId1, sku1,1, ProductOptions.empty());
        cart.addItem(productId2, sku2,2, ProductOptions.of(Map.of("color", "red")));
        cart.addItem(productId3, sku3,3, ProductOptions.empty());

        // 중간 아이템(productId2) 제거
        CartItemId itemIdToRemove = cart.getItems().get(1).getId();

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        RemoveItemFromCartCommand command = new RemoveItemFromCartCommand(cartId, itemIdToRemove);

        // when
        removeItemFromCartUseCase.execute(command);

        // then
        verify(cartRepository).save(argThat(savedCart ->
                savedCart.getItemCount() == 2 &&
                        savedCart.getTotalQuantity() == 4
        ));

        // 남은 아이템 확인
        assertThat(cart.getItems()).hasSize(2);
        assertThat(cart.getItems().stream()
                .map(item -> item.getProductId())
                .toList()).containsExactly(productId1, productId3);
    }

    @Test
    @DisplayName("동일 상품의 다른 옵션 아이템 중 하나만 제거")
    void execute_RemoveSameProductDifferentOption() {
        // given
        ProductId productId = ProductId.of("PROD-001");
        ProductOptions options1 = ProductOptions.of(Map.of("size", "L", "color", "black"));
        ProductOptions options2 = ProductOptions.of(Map.of("size", "M", "color", "white"));

        StockKeepingUnit sku1 = StockKeepingUnit.of("SKU-001");
        StockKeepingUnit sku2 = StockKeepingUnit.of("SKU-002");

        cart.addItem(productId, sku1, 2, options1);
        cart.addItem(productId, sku2, 3, options2);

        // 첫 번째 옵션 아이템 제거
        CartItemId itemIdToRemove = cart.getItems().get(0).getId();

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        RemoveItemFromCartCommand command = new RemoveItemFromCartCommand(cartId, itemIdToRemove);

        // when
        removeItemFromCartUseCase.execute(command);

        // then
        verify(cartRepository).save(argThat(savedCart ->
                savedCart.getItemCount() == 1 &&
                        savedCart.getTotalQuantity() == 3
        ));

        // 남은 아이템이 두 번째 옵션인지 확인
        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getOptions()).isEqualTo(options2);
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(3);
    }
}