package org.icd4.commerce.application.provided.cart;

import org.icd4.commerce.application.provided.cart.command.CreateCartCommand;
import org.icd4.commerce.application.provided.cart.usecase.CreateCartUseCase;
import org.icd4.commerce.application.required.CartRepositoryPort;
import org.icd4.commerce.domain.cart.Cart;
import org.icd4.commerce.domain.cart.CartId;
import org.icd4.commerce.domain.cart.CustomerId;
import org.icd4.commerce.domain.cart.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * CreateCartUseCase 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateCartUseCase 테스트")
class CreateCartUseCaseTest {
    
    @Mock
    private CartRepositoryPort cartRepository;
    
    @Mock
    private TimeProvider timeProvider;
    
    @InjectMocks
    private CreateCartUseCase createCartUseCase;
    
    private CustomerId customerId;
    private LocalDateTime now;
    
    @BeforeEach
    void setUp() {
        customerId = CustomerId.of("CUST-001");
        now = LocalDateTime.of(2024, 1, 1, 12, 0);
        
        when(timeProvider.now()).thenReturn(now);
    }
    
    @Test
    @DisplayName("새 장바구니 생성 성공")
    void execute_CreateNewCart_Success() {
        // given
        CreateCartCommand command = new CreateCartCommand(customerId);
        
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart cart = invocation.getArgument(0);
            return cart;
        });
        
        // when
        CartId result = createCartUseCase.execute(command);
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.value()).isNotBlank();
        
        verify(cartRepository).save(argThat(cart -> 
            cart.getId() != null &&
            cart.getCustomerId().equals(customerId) &&
            cart.getItemCount() == 0 &&
            cart.getTotalQuantity() == 0 &&
            cart.getCreatedAt().equals(now) &&
            cart.getLastModifiedAt().equals(now) &&
            !cart.isConverted()
        ));
    }
    
    @Test
    @DisplayName("동일 고객이 여러 장바구니 생성 가능")
    void execute_SameCustomerMultipleCarts_Success() {
        // given
        CreateCartCommand command1 = new CreateCartCommand(customerId);
        CreateCartCommand command2 = new CreateCartCommand(customerId);
        
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart cart = invocation.getArgument(0);
            return cart;
        });
        
        // when
        CartId result1 = createCartUseCase.execute(command1);
        CartId result2 = createCartUseCase.execute(command2);
        
        // then
        assertThat(result1).isNotNull();
        assertThat(result2).isNotNull();
        assertThat(result1).isNotEqualTo(result2);
        
        verify(cartRepository, times(2)).save(argThat(cart -> 
            cart.getCustomerId().equals(customerId)
        ));
    }
    
    @Test
    @DisplayName("CartId가 자동 생성되는지 확인")
    void execute_AutoGenerateCartId() {
        // given
        CreateCartCommand command = new CreateCartCommand(customerId);
        
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart cart = invocation.getArgument(0);
            return cart;
        });
        
        // when
        CartId result = createCartUseCase.execute(command);
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.value()).isNotBlank();
        // ULID 형식 검증 (26자리)
        assertThat(result.value()).hasSize(26);
    }
    
    @Test
    @DisplayName("Repository 저장 실패 시 예외 전파")
    void execute_RepositorySaveFailure() {
        // given
        CreateCartCommand command = new CreateCartCommand(customerId);
        
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(cartRepository.save(any(Cart.class))).thenThrow(repositoryException);
        
        // when & then
        assertThatThrownBy(() -> createCartUseCase.execute(command))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Database error");
        
        verify(cartRepository).save(any(Cart.class));
    }
    
    @Test
    @DisplayName("생성된 장바구니의 초기 상태 검증")
    void execute_VerifyInitialCartState() {
        // given
        CreateCartCommand command = new CreateCartCommand(customerId);
        
        Cart savedCart = null;
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart cart = invocation.getArgument(0);
            return cart;
        });
        
        // when
        CartId result = createCartUseCase.execute(command);
        
        // then
        verify(cartRepository).save(argThat(cart -> {
            // 초기 상태 검증
            assertThat(cart.getId()).isEqualTo(result);
            assertThat(cart.getCustomerId()).isEqualTo(customerId);
            assertThat(cart.getItems()).isEmpty();
            assertThat(cart.getItemCount()).isEqualTo(0);
            assertThat(cart.getTotalQuantity()).isEqualTo(0);
            assertThat(cart.isConverted()).isFalse();
            assertThat(cart.getCreatedAt()).isEqualTo(now);
            assertThat(cart.getLastModifiedAt()).isEqualTo(now);
            return true;
        }));
    }
    
    @Test
    @DisplayName("다른 고객의 장바구니 생성")
    void execute_DifferentCustomers_Success() {
        // given
        CustomerId customerId2 = CustomerId.of("CUST-002");
        CreateCartCommand command1 = new CreateCartCommand(customerId);
        CreateCartCommand command2 = new CreateCartCommand(customerId2);
        
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart cart = invocation.getArgument(0);
            return cart;
        });
        
        // when
        CartId result1 = createCartUseCase.execute(command1);
        CartId result2 = createCartUseCase.execute(command2);
        
        // then
        assertThat(result1).isNotEqualTo(result2);
        
        verify(cartRepository).save(argThat(cart -> 
            cart.getCustomerId().equals(customerId)
        ));
        verify(cartRepository).save(argThat(cart -> 
            cart.getCustomerId().equals(customerId2)
        ));
    }
}