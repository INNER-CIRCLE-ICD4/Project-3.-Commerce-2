package org.icd4.commerce.application;

import org.icd4.commerce.application.command.ProductCreationCommand;
import org.icd4.commerce.application.required.ProductRepository;
import org.icd4.commerce.domain.product.Product;
import org.icd4.commerce.domain.product.ProductMoney;
import org.icd4.commerce.domain.product.ProductOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class ProductCommandServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductCommandService productCommandService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    // 상품 삭제 테스트
    @Test
    @DisplayName("✅ ProductCommandService: 상품이 성공적으로 삭제되어야 한다 (String ID)")
    void shouldDeleteProductSuccessfullyWithStringId() {
        // Given
        String productIdToDelete = UUID.randomUUID().toString();

        Product existingProduct = Product.create(new ProductCreationCommand(
                "seller_001",
                "기존 상품",
                "브랜드",
                "설명",
                "CAT001",
                new ProductMoney(new BigDecimal("50.0"), "KRW"),
                Arrays.asList(
                        new ProductOption("color", "black"),
                        new ProductOption("color", "red")
                )
        ));
        existingProduct.setId(productIdToDelete); // Mocking을 위해 ID 설정

        when(productRepository.findById(productIdToDelete)).thenReturn(Optional.of(existingProduct));
        doNothing().when(productRepository).deleteById(productIdToDelete);

        // When
        productCommandService.deleteProduct(productIdToDelete);

        // Then
        verify(productRepository, times(1)).deleteById(productIdToDelete);
        verify(productRepository, times(1)).findById(productIdToDelete);
    }

    @Test
    @DisplayName("❌ ProductCommandService: 존재하지 않는 상품 삭제 시 예외가 발생해야 한다 (String ID)")
    void shouldThrowExceptionWhenDeletingNonExistentProductWithStringId() {
        // Given
        String nonExistentProductId = UUID.randomUUID().toString();
        when(productRepository.findById(nonExistentProductId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                        productCommandService.deleteProduct(nonExistentProductId),
                "존재하지 않는 상품 삭제 시 예외가 발생해야 합니다."
        );
        assertTrue(thrown.getMessage().contains("상품을 찾을 수 없습니다"), "예외 메시지가 예상과 다릅니다.");
        verify(productRepository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("❌ ProductCommandService: null 또는 빈 상품 ID로 삭제 시 예외가 발생해야 한다")
    void shouldThrowExceptionWhenDeletingWithNullOrEmptyProductId() {
        // Given
        String nullProductId = null;
        String emptyProductId = "";

        // When & Then for null ID
        IllegalArgumentException thrownNull = assertThrows(IllegalArgumentException.class, () ->
                        productCommandService.deleteProduct(nullProductId),
                "null ID 삭제 시 예외가 발생해야 합니다."
        );
        assertTrue(thrownNull.getMessage().contains("상품 ID는 필수입니다"), "Null ID 예외 메시지가 예상과 다릅니다.");
        verify(productRepository, never()).deleteById(anyString());

        // When & Then for empty ID
        IllegalArgumentException thrownEmpty = assertThrows(IllegalArgumentException.class, () ->
                        productCommandService.deleteProduct(emptyProductId),
                "빈 ID 삭제 시 예외가 발생해야 합니다."
        );
        assertTrue(thrownEmpty.getMessage().contains("상품 ID는 필수입니다"), "빈 ID 예외 메시지가 예상과 다릅니다.");
        verify(productRepository, never()).deleteById(anyString());
    }


    @Test
    @DisplayName("✅ ProductCommandService: 상품 가격이 성공적으로 변경되어야 한다")
    void shouldChangeProductPriceSuccessfully() {
        // Given (준비)
        String productId = UUID.randomUUID().toString();
        ProductMoney oldPrice = new ProductMoney(new BigDecimal("10000.0"), "KRW");
        ProductMoney newPrice = new ProductMoney(new BigDecimal("12500.0"), "KRW");

        // 기존 상품 객체 생성 및 ID 설정
        Product existingProduct = Product.create(new ProductCreationCommand(
                "seller_001", "테스트 상품", "브랜드", "설명", "CAT001", oldPrice, List.of()
        ));
        existingProduct.setId(productId);


        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        when(productRepository.save(any(Product.class))).thenReturn(existingProduct); // save 호출 시 기존 상품 객체 반환

        // When
        productCommandService.changeProductPrice(productId, newPrice);

        // Then
        verify(productRepository, times(1)).findById(productId);

        // productRepository.save가 1번 호출되었는지 검증
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).save(productCaptor.capture());
        Product capturedProduct = productCaptor.getValue();

        // 저장된 Product의 가격이 새로운 가격으로 변경되었는지 확인
        assertEquals(newPrice, capturedProduct.getPrice(), "상품의 가격이 새로운 가격으로 변경되어야 합니다.");
        // updatedAt 필드가 업데이트 되었는지 확인 (현재 시간과 거의 같아야 함)
        assertTrue(capturedProduct.getUpdatedAt().isAfter(LocalDateTime.now(ZoneOffset.UTC).minusSeconds(2)));
        assertTrue(capturedProduct.getUpdatedAt().isBefore(LocalDateTime.now(ZoneOffset.UTC).plusSeconds(2)));
    }

    @Test
    @DisplayName("❌ ProductCommandService: 존재하지 않는 상품의 가격 변경 시 예외가 발생해야 한다")
    void shouldThrowExceptionWhenChangingPriceOfNonExistentProduct() {
        // Given
        String nonExistentProductId = UUID.randomUUID().toString();
        ProductMoney newPrice = new ProductMoney(new BigDecimal("20000.0"), "KRW");

        when(productRepository.findById(nonExistentProductId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                        productCommandService.changeProductPrice(nonExistentProductId, newPrice),
                "존재하지 않는 상품의 가격 변경 시 예외가 발생해야 합니다."
        );
        assertTrue(thrown.getMessage().contains("상품을 찾을 수 없습니다: " + nonExistentProductId));

        // findById는 호출되었지만, save는 호출되지 않았는지 검증
        verify(productRepository, times(1)).findById(nonExistentProductId);
        verify(productRepository, never()).save(any(Product.class));
    }


    @Test
    @DisplayName("❌ ProductCommandService: 변경할 가격이 null이면 예외가 발생해야 한다")
    void shouldThrowExceptionWhenNewPriceIsNull() {
        // Given
        String productId = UUID.randomUUID().toString();
        ProductMoney newPrice = null; // null 가격

        // When & Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                        productCommandService.changeProductPrice(productId, newPrice),
                "가격이 null일 때 예외가 발생해야 합니다."
        );
        assertTrue(thrown.getMessage().contains("유효한 가격을 입력해야 합니다."));

        // findById, save 메서드는 호출되지 않았는지 검증
        verify(productRepository, never()).findById(anyString());
        verify(productRepository, never()).save(any(Product.class));
    }


    @Test
    @DisplayName("❌ ProductCommandService: 변경할 가격이 음수이면 예외가 발생해야 한다")
    void shouldThrowExceptionWhenNewPriceIsNegative() {
        // Given
        String productId = UUID.randomUUID().toString();
        ProductMoney newPrice = new ProductMoney(new BigDecimal("-500.0"), "KRW");

        // When & Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                        productCommandService.changeProductPrice(productId, newPrice),
                "가격이 음수일 때 예외가 발생해야 합니다."
        );
        assertTrue(thrown.getMessage().contains("유효한 가격을 입력해야 합니다."));

        // findById, save 메서드는 호출되지 않았는지 검증
        verify(productRepository, never()).findById(anyString());
        verify(productRepository, never()).save(any(Product.class));
    }


    @Test
    @DisplayName("❌ ProductCommandService: 가격 변경 시 상품 ID가 null이면 예외가 발생해야 한다")
    void shouldThrowExceptionWhenProductIdIsNullForPriceChange() {
        // Given
        String productId = null;
        ProductMoney newPrice =new ProductMoney(new BigDecimal("2000.0"), "KRW");

        // When & Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                        productCommandService.changeProductPrice(productId, newPrice),
                "상품 ID가 null일 때 예외가 발생해야 합니다."
        );
        assertTrue(thrown.getMessage().contains("상품 ID는 필수입니다."));

        verify(productRepository, never()).findById(anyString());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("❌ ProductCommandService: 가격 변경 시 상품 ID가 비어있으면 예외가 발생해야 한다")
    void shouldThrowExceptionWhenProductIdIsEmptyForPriceChange() {
        // Given
        String productId = "";
        ProductMoney newPrice = new ProductMoney(new BigDecimal("20000.0"), "KRW");

        // When & Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                        productCommandService.changeProductPrice(productId, newPrice),
                "상품 ID가 비어있을 때 예외가 발생해야 합니다."
        );
        assertTrue(thrown.getMessage().contains("상품 ID는 필수입니다."));

        verify(productRepository, never()).findById(anyString());
        verify(productRepository, never()).save(any(Product.class));
    }
}
