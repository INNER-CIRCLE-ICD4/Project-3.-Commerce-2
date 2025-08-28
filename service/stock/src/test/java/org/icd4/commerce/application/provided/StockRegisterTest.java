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
        assertThat(registeredStock.getSku()).isEqualTo(productId);
        assertThat(registeredStock.getQuantity()).isEqualTo(quantity);
        assertThat(registeredStock.getStockStatus()).isEqualTo(StockStatus.AVAILABLE);
        assertThat(registeredStock.getCreatedAt()).isNotNull();
        assertThat(registeredStock.getUpdatedAt()).isNotNull();

        // DB에 실제로 저장되었는지 확인
        Optional<Stock> savedStock = stockRepository.findBySku(registeredStock.getId());
        assertThat(savedStock).isPresent();
        assertThat(savedStock.get().getSku()).isEqualTo(productId);
        assertThat(savedStock.get().getQuantity()).isEqualTo(quantity);
    }

    @Test
    @DisplayName("재고 등록 - 실패 (null 상품 ID)")
    void register_Fail_NullProductId() {
        // Given
        String productId = null;
        Long quantity = 100L;

        // When & Then
        assertThatThrownBy(() -> stockRegister.register(productId, quantity))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("상품 ID를 입력해주세요");
    }

    @Test
    @DisplayName("재고 등록 - 실패 (0 이하의 수량)")
    void register_Fail_InvalidQuantity() {
        // Given
        String productId = "test-product-456";
        Long quantity = 0L;

        // When & Then
        assertThatThrownBy(() -> stockRegister.register(productId, quantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("재고의 값은 0 이하가 될 수 없습니다");
    }

    @Test
    @DisplayName("재고 수량 증가 - 성공")
    void increaseQuantity_Success() {
        // Given
        Stock stock = stockRepository.save(Stock.register("test-product-789", 50L));
        Long increaseAmount = 30L;

        // When
        stockRegister.increaseQuantity(stock.getId(), increaseAmount);

        // Then
        Optional<Stock> updatedStock = stockRepository.findBySku(stock.getId());
        assertThat(updatedStock).isPresent();
        assertThat(updatedStock.get().getQuantity()).isEqualTo(80L); // 50 + 30
    }

    @Test
    @DisplayName("재고 수량 증가 - 실패 (0 이하의 증가량)")
    void increaseQuantity_Fail_InvalidAmount() {
        // Given
        Stock stock = stockRepository.save(Stock.register("test-product-111", 50L));
        Long invalidAmount = 0L;

        // When & Then
        // 도메인 규칙에 의해 0 이하의 값으로 증가 시 예외 발생
        assertThatThrownBy(() -> stockRegister.increaseQuantity(stock.getId(), invalidAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("재고는 0 이하의 값이 될 수 없습니다");
        
        // 예외로 인해 재고 수량은 변하지 않아야 함
        Optional<Stock> unchangedStock = stockRepository.findBySku(stock.getId());
        assertThat(unchangedStock).isPresent();
        assertThat(unchangedStock.get().getQuantity()).isEqualTo(50L); // 변경되지 않음
    }

    @Test
    @DisplayName("재고 수량 감소 - 성공")
    void decreaseQuantity_Success() {
        // Given
        Stock stock = stockRepository.save(Stock.register("test-product-222", 100L));
        Long decreaseAmount = 30L;

        // When
        stockRegister.decreaseQuantity(stock.getId(), decreaseAmount);

        // Then
        Optional<Stock> updatedStock = stockRepository.findBySku(stock.getId());
        assertThat(updatedStock).isPresent();
        assertThat(updatedStock.get().getQuantity()).isEqualTo(70L); // 100 - 30
    }

    @Test
    @DisplayName("재고 수량 감소 - 실패 (재고 부족)")
    void decreaseQuantity_Fail_InsufficientStock() {
        // Given
        Stock stock = stockRepository.save(Stock.register("test-product-333", 30L));
        Long excessiveAmount = 50L;

        // When & Then
        // 도메인 규칙에 의해 재고 부족 시 예외 발생
        assertThatThrownBy(() -> stockRegister.decreaseQuantity(stock.getId(), excessiveAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("현재 재고보다 많습니다");
        
        // 예외로 인해 재고는 변하지 않아야 함
        Optional<Stock> unchangedStock = stockRepository.findBySku(stock.getId());
        assertThat(unchangedStock).isPresent();
        assertThat(unchangedStock.get().getQuantity()).isEqualTo(30L); // 변경되지 않음
    }

    @Test
    @DisplayName("존재하지 않는 재고 ID로 수량 증가 시도")
    void increaseQuantity_WithNonExistentStockId() {
        // Given
        String nonExistentStockId = "non-existent-stock-id";
        Long quantity = 10L;

        // When & Then
        // 존재하지 않는 재고 ID에 대해 예외 발생
        assertThatThrownBy(() -> stockRegister.increaseQuantity(nonExistentStockId, quantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock not found");
    }

    @Test
    @DisplayName("존재하지 않는 재고 ID로 수량 감소 시도")
    void decreaseQuantity_WithNonExistentStockId() {
        // Given
        String nonExistentStockId = "non-existent-stock-id";
        Long quantity = 10L;

        // When & Then
        // 존재하지 않는 재고 ID에 대해 예외 발생
        assertThatThrownBy(() -> stockRegister.decreaseQuantity(nonExistentStockId, quantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock not found");
    }

    @Test
    @DisplayName("대량 수량으로 재고 등록")
    void register_WithLargeQuantity() {
        // Given
        String productId = "test-product-large";
        Long largeQuantity = 1_000_000L;

        // When
        Stock registeredStock = stockRegister.register(productId, largeQuantity);

        // Then
        assertThat(registeredStock.getQuantity()).isEqualTo(largeQuantity);
        
        Optional<Stock> savedStock = stockRepository.findBySku(registeredStock.getId());
        assertThat(savedStock).isPresent();
        assertThat(savedStock.get().getQuantity()).isEqualTo(largeQuantity);
    }
} 