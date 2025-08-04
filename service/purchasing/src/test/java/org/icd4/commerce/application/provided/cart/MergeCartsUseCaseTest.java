package org.icd4.commerce.application.provided.cart;

import org.icd4.commerce.application.provided.cart.command.MergeCartsCommand;
import org.icd4.commerce.application.provided.cart.exception.CartNotFoundException;
import org.icd4.commerce.application.provided.cart.usecase.MergeCartsUseCase;
import org.icd4.commerce.application.required.CartRepositoryPort;
import org.icd4.commerce.domain.cart.*;
import org.icd4.commerce.domain.cart.exception.CartAlreadyConvertedException;
import org.icd4.commerce.domain.cart.exception.CartItemLimitExceededException;
import org.icd4.commerce.domain.cart.exception.InvalidCartStateException;
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
 * MergeCartsUseCase 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MergeCartsUseCase 테스트")
class MergeCartsUseCaseTest {
    
    @Mock
    private CartRepositoryPort cartRepository;
    
    @Mock
    private TimeProvider timeProvider;
    
    @InjectMocks
    private MergeCartsUseCase mergeCartsUseCase;
    
    private CartId targetCartId;
    private CartId sourceCartId;
    private Cart targetCart;
    private Cart sourceCart;
    private CustomerId customerId;
    
    @BeforeEach
    void setUp() {
        targetCartId = CartId.of("CART-TARGET");
        sourceCartId = CartId.of("CART-SOURCE");
        customerId = CustomerId.of("CUST-001");
        
        when(timeProvider.now()).thenReturn(LocalDateTime.of(2024, 1, 1, 12, 0));
        
        targetCart = new Cart(targetCartId, customerId, timeProvider);
        sourceCart = new Cart(sourceCartId, customerId, timeProvider);
    }
    
    @Test
    @DisplayName("장바구니 병합 후 소스 삭제")
    void execute_MergeAndDeleteSource_Success() {
        // given
        ProductId productId1 = ProductId.of("PROD-001");
        ProductId productId2 = ProductId.of("PROD-002");
        
        targetCart.addItem(productId1, 2, ProductOptions.empty());
        sourceCart.addItem(productId2, 3, ProductOptions.of(Map.of("size", "L")));
        
        when(cartRepository.findById(targetCartId)).thenReturn(Optional.of(targetCart));
        when(cartRepository.findById(sourceCartId)).thenReturn(Optional.of(sourceCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        MergeCartsCommand command = new MergeCartsCommand(targetCartId, sourceCartId, true);
        
        // when
        mergeCartsUseCase.execute(command);
        
        // then
        verify(cartRepository).findById(targetCartId);
        verify(cartRepository).findById(sourceCartId);
        verify(cartRepository).save(argThat(cart -> 
            cart.getId().equals(targetCartId) &&
            cart.getItemCount() == 2 &&
            cart.getTotalQuantity() == 5
        ));
        verify(cartRepository).deleteById(sourceCartId);
        
        // 타겟 카트에 소스 카트의 아이템이 추가되었는지 확인
        assertThat(targetCart.getItems()).hasSize(2);
        assertThat(targetCart.getTotalQuantity()).isEqualTo(5);
    }
    
    @Test
    @DisplayName("장바구니 병합 후 소스 비우기")
    void execute_MergeAndClearSource_Success() {
        // given
        ProductId productId1 = ProductId.of("PROD-001");
        ProductId productId2 = ProductId.of("PROD-002");
        
        targetCart.addItem(productId1, 2, ProductOptions.empty());
        sourceCart.addItem(productId2, 3, ProductOptions.of(Map.of("size", "L")));
        
        when(cartRepository.findById(targetCartId)).thenReturn(Optional.of(targetCart));
        when(cartRepository.findById(sourceCartId)).thenReturn(Optional.of(sourceCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        MergeCartsCommand command = new MergeCartsCommand(targetCartId, sourceCartId, false);
        
        // when
        mergeCartsUseCase.execute(command);
        
        // then
        verify(cartRepository, times(2)).save(any(Cart.class));
        verify(cartRepository).save(argThat(cart -> 
            cart.getId().equals(targetCartId) &&
            cart.getItemCount() == 2
        ));
        verify(cartRepository).save(argThat(cart -> 
            cart.getId().equals(sourceCartId) &&
            cart.getItemCount() == 0
        ));
        verify(cartRepository, never()).deleteById(any());
        
        // 소스 카트가 비워졌는지 확인
        assertThat(sourceCart.getItems()).isEmpty();
        assertThat(sourceCart.getTotalQuantity()).isEqualTo(0);
    }
    
    @Test
    @DisplayName("동일 상품 병합 시 수량 합산")
    void execute_MergeSameProduct_SumQuantity() {
        // given
        ProductId productId = ProductId.of("PROD-001");
        ProductOptions options = ProductOptions.of(Map.of("size", "L"));
        
        targetCart.addItem(productId, 2, options);
        sourceCart.addItem(productId, 3, options);
        
        when(cartRepository.findById(targetCartId)).thenReturn(Optional.of(targetCart));
        when(cartRepository.findById(sourceCartId)).thenReturn(Optional.of(sourceCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        MergeCartsCommand command = new MergeCartsCommand(targetCartId, sourceCartId, true);
        
        // when
        mergeCartsUseCase.execute(command);
        
        // then
        verify(cartRepository).save(argThat(cart -> 
            cart.getId().equals(targetCartId) &&
            cart.getItemCount() == 1 &&
            cart.getTotalQuantity() == 5
        ));
        
        // 수량이 합산되었는지 확인
        assertThat(targetCart.getItems()).hasSize(1);
        assertThat(targetCart.getItems().get(0).getQuantity()).isEqualTo(5);
    }
    
    @Test
    @DisplayName("타겟 장바구니를 찾을 수 없는 경우")
    void execute_TargetCartNotFound() {
        // given
        when(cartRepository.findById(targetCartId)).thenReturn(Optional.empty());
        
        MergeCartsCommand command = new MergeCartsCommand(targetCartId, sourceCartId, true);
        
        // when & then
        assertThatThrownBy(() -> mergeCartsUseCase.execute(command))
            .isInstanceOf(CartNotFoundException.class)
            .hasMessageContaining(targetCartId.value());
        
        verify(cartRepository).findById(targetCartId);
        verify(cartRepository, never()).findById(sourceCartId);
        verify(cartRepository, never()).save(any());
        verify(cartRepository, never()).deleteById(any());
    }
    
    @Test
    @DisplayName("소스 장바구니를 찾을 수 없는 경우")
    void execute_SourceCartNotFound() {
        // given
        when(cartRepository.findById(targetCartId)).thenReturn(Optional.of(targetCart));
        when(cartRepository.findById(sourceCartId)).thenReturn(Optional.empty());
        
        MergeCartsCommand command = new MergeCartsCommand(targetCartId, sourceCartId, true);
        
        // when & then
        assertThatThrownBy(() -> mergeCartsUseCase.execute(command))
            .isInstanceOf(CartNotFoundException.class)
            .hasMessageContaining(sourceCartId.value());
        
        verify(cartRepository).findById(targetCartId);
        verify(cartRepository).findById(sourceCartId);
        verify(cartRepository, never()).save(any());
        verify(cartRepository, never()).deleteById(any());
    }
    
    @Test
    @DisplayName("타겟 장바구니가 이미 전환된 경우")
    void execute_TargetCartAlreadyConverted() {
        // given
        targetCart.addItem(ProductId.of("PROD-001"), 1, ProductOptions.empty());
        targetCart.convertToOrder();
        sourceCart.addItem(ProductId.of("PROD-002"), 1, ProductOptions.empty());
        
        when(cartRepository.findById(targetCartId)).thenReturn(Optional.of(targetCart));
        when(cartRepository.findById(sourceCartId)).thenReturn(Optional.of(sourceCart));
        
        MergeCartsCommand command = new MergeCartsCommand(targetCartId, sourceCartId, true);
        
        // when & then
        assertThatThrownBy(() -> mergeCartsUseCase.execute(command))
            .isInstanceOf(CartAlreadyConvertedException.class);
        
        verify(cartRepository).findById(targetCartId);
        verify(cartRepository).findById(sourceCartId);
        verify(cartRepository, never()).save(any());
        verify(cartRepository, never()).deleteById(any());
    }
    
    @Test
    @DisplayName("소스 장바구니가 이미 전환된 경우")
    void execute_SourceCartAlreadyConverted() {
        // given
        targetCart.addItem(ProductId.of("PROD-001"), 1, ProductOptions.empty());
        sourceCart.addItem(ProductId.of("PROD-002"), 1, ProductOptions.empty());
        sourceCart.convertToOrder();
        
        when(cartRepository.findById(targetCartId)).thenReturn(Optional.of(targetCart));
        when(cartRepository.findById(sourceCartId)).thenReturn(Optional.of(sourceCart));
        
        MergeCartsCommand command = new MergeCartsCommand(targetCartId, sourceCartId, true);
        
        // when & then
        assertThatThrownBy(() -> mergeCartsUseCase.execute(command))
            .isInstanceOf(InvalidCartStateException.class);
        
        verify(cartRepository).findById(targetCartId);
        verify(cartRepository).findById(sourceCartId);
        verify(cartRepository, never()).save(any());
        verify(cartRepository, never()).deleteById(any());
    }
    
    @Test
    @DisplayName("병합 시 상품 종류 50개 초과")
    void execute_ExceedItemLimitAfterMerge() {
        // given
        // 타겟 카트에 30개 상품 추가
        for (int i = 1; i <= 30; i++) {
            targetCart.addItem(
                ProductId.of("PROD-TARGET-" + String.format("%03d", i)),
                1,
                ProductOptions.empty()
            );
        }
        
        // 소스 카트에 25개 상품 추가 (병합 시 총 55개가 됨)
        for (int i = 1; i <= 25; i++) {
            sourceCart.addItem(
                ProductId.of("PROD-SOURCE-" + String.format("%03d", i)),
                1,
                ProductOptions.empty()
            );
        }
        
        when(cartRepository.findById(targetCartId)).thenReturn(Optional.of(targetCart));
        when(cartRepository.findById(sourceCartId)).thenReturn(Optional.of(sourceCart));
        
        MergeCartsCommand command = new MergeCartsCommand(targetCartId, sourceCartId, true);
        
        // when & then
        assertThatThrownBy(() -> mergeCartsUseCase.execute(command))
            .isInstanceOf(CartItemLimitExceededException.class);
        
        verify(cartRepository).findById(targetCartId);
        verify(cartRepository).findById(sourceCartId);
        verify(cartRepository, never()).save(any());
        verify(cartRepository, never()).deleteById(any());
    }
    
    @Test
    @DisplayName("빈 소스 장바구니 병합")
    void execute_EmptySourceCart_Success() {
        // given
        targetCart.addItem(ProductId.of("PROD-001"), 2, ProductOptions.empty());
        // sourceCart는 비어있음
        
        when(cartRepository.findById(targetCartId)).thenReturn(Optional.of(targetCart));
        when(cartRepository.findById(sourceCartId)).thenReturn(Optional.of(sourceCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        MergeCartsCommand command = new MergeCartsCommand(targetCartId, sourceCartId, true);
        
        // when
        mergeCartsUseCase.execute(command);
        
        // then
        verify(cartRepository).save(targetCart);
        verify(cartRepository).deleteById(sourceCartId);
        
        // 타겟 카트가 변경되지 않았는지 확인
        assertThat(targetCart.getItemCount()).isEqualTo(1);
        assertThat(targetCart.getTotalQuantity()).isEqualTo(2);
    }
}