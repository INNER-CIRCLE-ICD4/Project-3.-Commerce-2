package org.icd4.commerce.application.provided.cart;

import org.icd4.commerce.application.provided.cart.command.UpdateCartItemQuantityCommand;
import org.icd4.commerce.application.provided.cart.exception.CartNotFoundException;
import org.icd4.commerce.application.provided.cart.usecase.UpdateCartItemQuantityUseCase;
import org.icd4.commerce.application.required.cart.CartRepositoryPort;
import org.icd4.commerce.domain.cart.*;
import org.icd4.commerce.domain.cart.exception.CartAlreadyConvertedException;
import org.icd4.commerce.domain.cart.exception.InvalidCartStateException;
import org.icd4.commerce.domain.common.ProductId;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UpdateCartItemQuantityUseCase 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateCartItemQuantityUseCase 테스트")
class UpdateCartItemQuantityUseCaseTest {
    
    @Mock
    private CartRepositoryPort cartRepository;
    
    @Mock
    private TimeProvider timeProvider;
    
    @InjectMocks
    private UpdateCartItemQuantityUseCase updateCartItemQuantityUseCase;
    
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
    @DisplayName("수량 증가 성공")
    void execute_IncreaseQuantity_Success() {
        // given
        ProductId productId = ProductId.of("PROD-001");
        cart.addItem(productId, 3, ProductOptions.empty());
        
        CartItemId cartItemId = cart.getItems().get(0).getId();
        
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        
        UpdateCartItemQuantityCommand command = new UpdateCartItemQuantityCommand(cartId, cartItemId, 5);
        
        // when
        updateCartItemQuantityUseCase.execute(command);
        
        // then
        verify(cartRepository).findById(cartId);
        verify(cartRepository).save(argThat(savedCart -> 
            savedCart.getItems().get(0).getQuantity() == 5 &&
            savedCart.getTotalQuantity() == 5
        ));
        
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(5);
    }
    
    @Test
    @DisplayName("수량 감소 성공")
    void execute_DecreaseQuantity_Success() {
        // given
        ProductId productId = ProductId.of("PROD-001");
        cart.addItem(productId, 5, ProductOptions.of(Map.of("size", "L")));
        
        CartItemId cartItemId = cart.getItems().get(0).getId();
        
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        
        UpdateCartItemQuantityCommand command = new UpdateCartItemQuantityCommand(cartId, cartItemId, 2);
        
        // when
        updateCartItemQuantityUseCase.execute(command);
        
        // then
        verify(cartRepository).save(argThat(savedCart -> 
            savedCart.getItems().get(0).getQuantity() == 2 &&
            savedCart.getTotalQuantity() == 2
        ));
        
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("최소 수량 경계값 테스트 (1)")
    void execute_MinimumQuantity_Success() {
        // given
        ProductId productId = ProductId.of("PROD-001");
        cart.addItem(productId, 10, ProductOptions.empty());
        
        CartItemId cartItemId = cart.getItems().get(0).getId();
        
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        
        UpdateCartItemQuantityCommand command = new UpdateCartItemQuantityCommand(cartId, cartItemId, 1);
        
        // when
        updateCartItemQuantityUseCase.execute(command);
        
        // then
        verify(cartRepository).save(argThat(savedCart -> 
            savedCart.getItems().get(0).getQuantity() == 1
        ));
        
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(1);
    }
    
    @Test
    @DisplayName("최대 수량 경계값 테스트 (99)")
    void execute_MaximumQuantity_Success() {
        // given
        ProductId productId = ProductId.of("PROD-001");
        cart.addItem(productId, 50, ProductOptions.empty());
        
        CartItemId cartItemId = cart.getItems().get(0).getId();
        
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        
        UpdateCartItemQuantityCommand command = new UpdateCartItemQuantityCommand(cartId, cartItemId, 99);
        
        // when
        updateCartItemQuantityUseCase.execute(command);
        
        // then
        verify(cartRepository).save(argThat(savedCart -> 
            savedCart.getItems().get(0).getQuantity() == 99
        ));
        
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(99);
    }
    
    @Test
    @DisplayName("장바구니를 찾을 수 없는 경우")
    void execute_CartNotFound() {
        // given
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());
        
        CartItemId cartItemId = CartItemId.of("ITEM-001");
        UpdateCartItemQuantityCommand command = new UpdateCartItemQuantityCommand(cartId, cartItemId, 5);
        
        // when & then
        assertThatThrownBy(() -> updateCartItemQuantityUseCase.execute(command))
            .isInstanceOf(CartNotFoundException.class)
            .hasMessageContaining(cartId.value());
        
        verify(cartRepository).findById(cartId);
        verify(cartRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("존재하지 않는 아이템 수량 변경 시도")
    void execute_ItemNotFound() {
        // given
        ProductId productId = ProductId.of("PROD-001");
        cart.addItem(productId, 3, ProductOptions.empty());
        
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        
        CartItemId nonExistentItemId = CartItemId.of("NON-EXISTENT-ITEM");
        UpdateCartItemQuantityCommand command = new UpdateCartItemQuantityCommand(cartId, nonExistentItemId, 5);
        
        // when & then
        assertThatThrownBy(() -> updateCartItemQuantityUseCase.execute(command))
            .isInstanceOf(InvalidCartStateException.class);
        
        verify(cartRepository).findById(cartId);
        verify(cartRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("유효하지 않은 수량 (0)")
    void execute_InvalidQuantityZero() {
        // given
        CartItemId cartItemId = CartItemId.of("ITEM-001");
        
        // when & then - Command 생성 시 수량 검증
        assertThatThrownBy(() -> new UpdateCartItemQuantityCommand(cartId, cartItemId, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Quantity must be between 1 and 99");
        
        verify(cartRepository, never()).findById(any());
        verify(cartRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("유효하지 않은 수량 (100)")
    void execute_InvalidQuantityHundred() {
        // given
        CartItemId cartItemId = CartItemId.of("ITEM-001");
        
        // when & then - Command 생성 시 수량 검증
        assertThatThrownBy(() -> new UpdateCartItemQuantityCommand(cartId, cartItemId, 100))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Quantity must be between 1 and 99");
        
        verify(cartRepository, never()).findById(any());
        verify(cartRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("이미 전환된 장바구니에서 수량 변경 시도")
    void execute_AlreadyConverted() {
        // given
        ProductId productId = ProductId.of("PROD-001");
        cart.addItem(productId, 3, ProductOptions.empty());
        CartItemId cartItemId = cart.getItems().get(0).getId();
        cart.convertToOrder();
        
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        
        UpdateCartItemQuantityCommand command = new UpdateCartItemQuantityCommand(cartId, cartItemId, 5);
        
        // when & then
        assertThatThrownBy(() -> updateCartItemQuantityUseCase.execute(command))
            .isInstanceOf(CartAlreadyConvertedException.class);
        
        verify(cartRepository).findById(cartId);
        verify(cartRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("여러 아이템 중 특정 아이템의 수량만 변경")
    void execute_UpdateSpecificItemQuantity() {
        // given
        ProductId productId1 = ProductId.of("PROD-001");
        ProductId productId2 = ProductId.of("PROD-002");
        ProductId productId3 = ProductId.of("PROD-003");
        
        cart.addItem(productId1, 2, ProductOptions.empty());
        cart.addItem(productId2, 3, ProductOptions.of(Map.of("color", "red")));
        cart.addItem(productId3, 4, ProductOptions.empty());
        
        // 두 번째 아이템의 수량 변경
        CartItemId cartItemId = cart.getItems().get(1).getId();
        
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        
        UpdateCartItemQuantityCommand command = new UpdateCartItemQuantityCommand(cartId, cartItemId, 7);
        
        // when
        updateCartItemQuantityUseCase.execute(command);
        
        // then
        verify(cartRepository).save(argThat(savedCart -> 
            savedCart.getItems().get(0).getQuantity() == 2 &&
            savedCart.getItems().get(1).getQuantity() == 7 &&
            savedCart.getItems().get(2).getQuantity() == 4 &&
            savedCart.getTotalQuantity() == 13
        ));
        
        // 다른 아이템들은 영향받지 않았는지 확인
        assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(cart.getItems().get(1).getQuantity()).isEqualTo(7);
        assertThat(cart.getItems().get(2).getQuantity()).isEqualTo(4);
    }
}