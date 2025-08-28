package org.icd4.commerce.application.provided.cart;

import org.icd4.commerce.application.provided.cart.command.AddItemToCartCommand;
import org.icd4.commerce.application.provided.cart.exception.CartNotFoundException;
import org.icd4.commerce.application.provided.cart.exception.InsufficientStockException;
import org.icd4.commerce.application.provided.cart.usecase.AddItemToCartUseCase;
import org.icd4.commerce.application.required.cart.CartRepositoryPort;
import org.icd4.commerce.application.required.common.InventoryChecker;
import org.icd4.commerce.domain.cart.*;
import org.icd4.commerce.domain.cart.exception.CartAlreadyConvertedException;
import org.icd4.commerce.domain.cart.exception.CartItemLimitExceededException;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AddItemToCartUseCase 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AddItemToCartUseCase 테스트")
class AddItemToCartUseCaseTest {

    @Mock
    private CartRepositoryPort cartRepository;

    @Mock
    private InventoryChecker inventoryChecker;

    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private AddItemToCartUseCase addItemToCartUseCase;

    private CartId cartId;
    private Cart cart;
    private ProductId productId;
    private StockKeepingUnit sku;
    private ProductOptions options;

    @BeforeEach
    void setUp() {
        cartId = CartId.of("CART-001");
        CustomerId customerId = CustomerId.of("CUST-001");
        productId = ProductId.of("PROD-001");
        sku = StockKeepingUnit.of("SKU");
        options = ProductOptions.of(Map.of("size", "L", "color", "black"));

        when(timeProvider.now()).thenReturn(LocalDateTime.of(2024, 1, 1, 12, 0));

        cart = new Cart(cartId, customerId, timeProvider);
    }

    @Test
    @DisplayName("신규 상품 추가 성공")
    void execute_AddNewItem_Success() {
        // given
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(inventoryChecker.getAvailableStock(sku)).thenReturn(new InventoryChecker.AvailableStock(10));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        AddItemToCartCommand command = new AddItemToCartCommand(cartId, productId, sku, 3, options);

        // when
        addItemToCartUseCase.execute(command);

        // then
        verify(cartRepository).findById(cartId);
        verify(cartRepository).save(argThat(savedCart ->
                savedCart.getItemCount() == 1 &&
                        savedCart.getTotalQuantity() == 3
        ));
    }

    @Test
    @DisplayName("기존 상품과 동일한 옵션으로 추가 시 수량 증가")
    void execute_AddExistingItemWithSameOptions_IncreaseQuantity() {
        // given
        cart.addItem(productId, sku, 2, options);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(inventoryChecker.getAvailableStock(sku)).thenReturn(new InventoryChecker.AvailableStock(10));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        AddItemToCartCommand command = new AddItemToCartCommand(cartId, productId, sku, 3, options);

        // when
        addItemToCartUseCase.execute(command);

        // then
        verify(cartRepository).save(argThat(savedCart ->
                savedCart.getItemCount() == 1 &&
                        savedCart.getTotalQuantity() == 5
        ));
    }

    @Test
    @DisplayName("기존 상품과 다른 옵션으로 추가 시 별도 항목 생성")
    void execute_AddExistingItemWithDifferentOptions_CreateSeparateItem() {
        // given
        cart.addItem(productId, sku, 2, options);

        ProductOptions differentOptions = ProductOptions.of(Map.of("size", "M", "color", "white"));

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(inventoryChecker.getAvailableStock(sku)).thenReturn(new InventoryChecker.AvailableStock(10));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        AddItemToCartCommand command = new AddItemToCartCommand(cartId, productId, sku, 1, differentOptions);

        // when
        addItemToCartUseCase.execute(command);

        // then
        verify(cartRepository).save(argThat(savedCart ->
                savedCart.getItemCount() == 2 &&
                        savedCart.getTotalQuantity() == 3
        ));
    }

    @Test
    @DisplayName("장바구니를 찾을 수 없는 경우")
    void execute_CartNotFound() {
        // given
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        AddItemToCartCommand command = new AddItemToCartCommand(cartId, productId, sku, 3, options);

        // when & then
        assertThatThrownBy(() -> addItemToCartUseCase.execute(command))
                .isInstanceOf(CartNotFoundException.class)
                .hasMessageContaining(cartId.value());

        verify(cartRepository).findById(cartId);
        verify(inventoryChecker, never()).getAvailableStock(any());
        verify(cartRepository, never()).save(any());
    }

    @Test
    @DisplayName("재고 부족한 경우")
    void execute_InsufficientStock() {
        // given
        cart.addItem(productId, sku, 5, options);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(inventoryChecker.getAvailableStock(sku)).thenReturn(new InventoryChecker.AvailableStock(7));

        AddItemToCartCommand command = new AddItemToCartCommand(cartId, productId, sku, 3, options);

        // when & then
        assertThatThrownBy(() -> addItemToCartUseCase.execute(command))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("Insufficient stock for product PROD-001. Available: 7, Requested: 8");

        verify(cartRepository).findById(cartId);
        verify(inventoryChecker).getAvailableStock(sku);
        verify(cartRepository, never()).save(any());
    }

    @Test
    @DisplayName("이미 전환된 장바구니에 추가 시도")
    void execute_AlreadyConverted() {
        // given
        cart.addItem(ProductId.of("PROD-999"), sku, 1, ProductOptions.empty());
        cart.convertToOrder();

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(inventoryChecker.getAvailableStock(sku)).thenReturn(new InventoryChecker.AvailableStock(10));

        AddItemToCartCommand command = new AddItemToCartCommand(cartId, productId, sku, 3, options);

        // when & then
        assertThatThrownBy(() -> addItemToCartUseCase.execute(command))
                .isInstanceOf(CartAlreadyConvertedException.class);

        verify(cartRepository).findById(cartId);
        verify(inventoryChecker).getAvailableStock(sku);
        verify(cartRepository, never()).save(any());
    }

    @Test
    @DisplayName("장바구니 상품 종류 50개 초과 시도")
    void execute_ExceedItemLimit() {
        // given
        // 50개의 서로 다른 상품 추가
        for (int i = 1; i <= 50; i++) {
            cart.addItem(
                    ProductId.of("PROD-" + String.format("%03d", i)),
                    sku,
                    1,
                    ProductOptions.empty()
            );
        }

        ProductId newProductId = ProductId.of("PROD-051");
        StockKeepingUnit newSku = StockKeepingUnit.of("SKU-051");

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(inventoryChecker.getAvailableStock(newSku)).thenReturn(new InventoryChecker.AvailableStock(10));

        AddItemToCartCommand command = new AddItemToCartCommand(cartId, newProductId, newSku, 1, ProductOptions.empty());

        // when & then
        assertThatThrownBy(() -> addItemToCartUseCase.execute(command))
                .isInstanceOf(CartItemLimitExceededException.class);

        verify(cartRepository).findById(cartId);
        verify(inventoryChecker).getAvailableStock(newSku);
        verify(cartRepository, never()).save(any());
    }

    @Test
    @DisplayName("유효하지 않은 수량으로 추가 시도")
    void execute_InvalidQuantity() {

        // when & then - 수량 0
        assertThatThrownBy(() -> new AddItemToCartCommand(cartId, productId, sku,0, options))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity must be between 1 and 99");

        // when & then - 수량 100
        assertThatThrownBy(() -> new AddItemToCartCommand(cartId, productId, sku,100, options))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Quantity must be between 1 and 99");
    }

    @Test
    @DisplayName("재고가 정확히 맞는 경우 추가 성공")
    void execute_ExactStock_Success() {
        // given
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(inventoryChecker.getAvailableStock(sku)).thenReturn(new InventoryChecker.AvailableStock(5));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        AddItemToCartCommand command = new AddItemToCartCommand(cartId, productId, sku, 5, options);

        // when
        addItemToCartUseCase.execute(command);

        // then
        verify(cartRepository).save(argThat(savedCart ->
                savedCart.getTotalQuantity() == 5
        ));
    }
}