package org.icd4.commerce.application.provided;

import org.icd4.commerce.application.required.StockRepository;
import org.icd4.commerce.domain.Stock;
import org.icd4.commerce.domain.StockStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class StockRegisterTest {

    @Autowired
    private StockRegister stockRegister;

    @Autowired
    private StockRepository stockRepository;

    @Test
    @DisplayName("재고 등록 - 성공")
    void register_Success() {
        // Given
        String productId = "test-product-123";
        Long quantity = 100L;

        // When
        Stock registeredStock = stockRegister.register(productId, quantity);

        // Then
        assertThat(registeredStock).isNotNull();
        assertThat(registeredStock.getId()).isNotNull();
        assertThat(registeredStock.getProductId()).isEqualTo(productId);
        assertThat(registeredStock.getQuantity()).isEqualTo(quantity);
        assertThat(registeredStock.getStockStatus()).isEqualTo(StockStatus.AVAILABLE);
        assertThat(registeredStock.getCreatedAt()).isNotNull();
        assertThat(registeredStock.getUpdatedAt()).isNotNull();

        // DB에 실제로 저장되었는지 확인
        Optional<Stock> savedStock = stockRepository.findById(registeredStock.getId());
        assertThat(savedStock).isPresent();
        assertThat(savedStock.get().getProductId()).isEqualTo(productId);
        assertThat(savedStock.get().getQuantity()).isEqualTo(quantity);
    }

    @Test
    @DisplayName("재고 등록 - 실패 (0 이하의 수량)")
    void register_Fail_InvalidQuantity() {
        // Given
        String productId = "test-product-123";
        Long invalidQuantity = 0L;

        // When & Then
        assertThatThrownBy(() -> stockRegister.register(productId, invalidQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고의 값은 0 이하가 될 수 없습니다.");
    }

    @Test
    @DisplayName("재고 등록 - 실패 (null 상품 ID)")
    void register_Fail_NullProductId() {
        // Given
        String nullProductId = null;
        Long quantity = 100L;

        // When & Then
        assertThatThrownBy(() -> stockRegister.register(nullProductId, quantity))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("상품 ID를 입력해주세요.");
    }

    @Test
    @DisplayName("재고 수량 증가 - 성공")
    void increaseQuantity_Success() {
        // Given
        String productId = "test-product-123";
        Long initialQuantity = 100L;
        Long increaseAmount = 50L;

        Stock registeredStock = stockRegister.register(productId, initialQuantity);
        String stockId = registeredStock.getId();

        // When
        stockRegister.increaseQuantity(stockId, increaseAmount);

        // Then
        Optional<Stock> updatedStock = stockRepository.findById(stockId);
        assertThat(updatedStock).isPresent();
        assertThat(updatedStock.get().getQuantity()).isEqualTo(initialQuantity + increaseAmount);
    }

    @Test
    @DisplayName("재고 수량 증가 - 실패 (존재하지 않는 재고 ID)")
    void increaseQuantity_Fail_NonExistentStockId() {
        // Given
        String nonExistentStockId = "non-existent-id";
        Long increaseAmount = 50L;

        // When & Then
        // 존재하지 않는 재고 ID에 대해서는 아무 일도 일어나지 않음 (현재 구현 기준)
        assertThatCode(() -> stockRegister.increaseQuantity(nonExistentStockId, increaseAmount))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("재고 수량 감소 - 성공")
    void decreaseQuantity_Success() {
        // Given
        String productId = "test-product-123";
        Long initialQuantity = 100L;
        Long decreaseAmount = 30L;

        Stock registeredStock = stockRegister.register(productId, initialQuantity);
        String stockId = registeredStock.getId();

        // When
        stockRegister.decreaseQuantity(stockId, decreaseAmount);

        // Then
        Optional<Stock> updatedStock = stockRepository.findById(stockId);
        assertThat(updatedStock).isPresent();
        assertThat(updatedStock.get().getQuantity()).isEqualTo(initialQuantity - decreaseAmount);
    }

    @Test
    @DisplayName("재고 수량 감소 - 실패 (존재하지 않는 재고 ID)")
    void decreaseQuantity_Fail_NonExistentStockId() {
        // Given
        String nonExistentStockId = "non-existent-id";
        Long decreaseAmount = 30L;

        // When & Then
        // 존재하지 않는 재고 ID에 대해서는 아무 일도 일어나지 않음 (현재 구현 기준)
        assertThatCode(() -> stockRegister.decreaseQuantity(nonExistentStockId, decreaseAmount))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("재고 수량 연속 변경 - 성공")
    void multipleQuantityChanges_Success() {
        // Given
        String productId = "test-product-123";
        Long initialQuantity = 100L;

        Stock registeredStock = stockRegister.register(productId, initialQuantity);
        String stockId = registeredStock.getId();

        // When
        stockRegister.increaseQuantity(stockId, 50L);  // 100 + 50 = 150
        stockRegister.decreaseQuantity(stockId, 30L);  // 150 - 30 = 120
        stockRegister.increaseQuantity(stockId, 20L);  // 120 + 20 = 140

        // Then
        Optional<Stock> finalStock = stockRepository.findById(stockId);
        assertThat(finalStock).isPresent();
        assertThat(finalStock.get().getQuantity()).isEqualTo(140L);
    }
}