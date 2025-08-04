package org.icd4.commerce.application.provided.cart;

import org.icd4.commerce.application.provided.cart.command.ClearCartCommand;
import org.icd4.commerce.application.provided.cart.exception.CartNotFoundException;
import org.icd4.commerce.application.provided.cart.usecase.ClearCartUseCase;
import org.icd4.commerce.application.required.CartRepositoryPort;
import org.icd4.commerce.domain.cart.Cart;
import org.icd4.commerce.domain.cart.CartId;
import org.icd4.commerce.domain.cart.CustomerId;
import org.icd4.commerce.domain.cart.ProductId;
import org.icd4.commerce.domain.cart.ProductOptions;
import org.icd4.commerce.domain.cart.TimeProvider;
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
 * ClearCartUseCase 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClearCartUseCase 테스트")
class ClearCartUseCaseTest {
    
    @Mock
    private CartRepositoryPort cartRepository;
    
    @Mock
    private TimeProvider timeProvider;
    
    @InjectMocks
    private ClearCartUseCase clearCartUseCase;
    
    private CartId cartId;
    private Cart cart;
    
    @BeforeEach
    void setUp() {
        cartId = CartId.of("CART-001");
        CustomerId customerId = CustomerId.of("CUST-001");
        
        when(timeProvider.now()).thenReturn(LocalDateTime.of(2024, 1, 1, 12, 0));
        
        cart = new Cart(cartId, customerId, timeProvider);
    }
    
    @Test
    @DisplayName("장바구니 비우기 성공")
    void execute_Success() {
        // given
        // 장바구니에 아이템 추가
        cart.addItem(
            ProductId.of("PROD-001"),
            2,
            ProductOptions.of(Map.of("size", "L"))
        );
        cart.addItem(
            ProductId.of("PROD-002"),
            1,
            ProductOptions.empty()
        );
        
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        
        ClearCartCommand command = new ClearCartCommand(cartId);
        
        // when
        clearCartUseCase.execute(command);
        
        // then
        verify(cartRepository).findById(cartId);
        verify(cartRepository).save(argThat(savedCart -> 
            savedCart.getItemCount() == 0
        ));
    }
    
    @Test
    @DisplayName("이미 빈 장바구니 비우기")
    void execute_AlreadyEmpty() {
        // given
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        
        ClearCartCommand command = new ClearCartCommand(cartId);
        
        // when
        clearCartUseCase.execute(command);
        
        // then
        verify(cartRepository).findById(cartId);
        verify(cartRepository).save(cart);
        assertThat(cart.getItemCount()).isEqualTo(0);
    }
    
    @Test
    @DisplayName("존재하지 않는 장바구니 비우기 시도")
    void execute_CartNotFound() {
        // given
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());
        
        ClearCartCommand command = new ClearCartCommand(cartId);
        
        // when & then
        assertThatThrownBy(() -> clearCartUseCase.execute(command))
            .isInstanceOf(CartNotFoundException.class)
            .hasMessageContaining(cartId.value());
        
        verify(cartRepository).findById(cartId);
        verify(cartRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("이미 전환된 장바구니 비우기 시도")
    void execute_AlreadyConverted() {
        // given
        // 장바구니에 아이템 추가 후 전환
        cart.addItem(
            ProductId.of("PROD-001"),
            1,
            ProductOptions.empty()
        );
        cart.convertToOrder();
        
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        
        ClearCartCommand command = new ClearCartCommand(cartId);
        
        // when & then
        assertThatThrownBy(() -> clearCartUseCase.execute(command))
            .isInstanceOf(Exception.class);  // CartAlreadyConvertedException
        
        verify(cartRepository).findById(cartId);
        verify(cartRepository, never()).save(any());
    }
}