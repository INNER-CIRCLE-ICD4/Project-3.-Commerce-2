package org.icd4.commerce.application;

import org.icd4.commerce.application.required.StockRepository;
import org.icd4.commerce.domain.Stock;
import org.icd4.commerce.domain.StockStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockServiceV1Test {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockService stockService;

    @Test
    @DisplayName("increaseQuantityV1 - 성공")
    void increaseQuantityV1_Success() {
        // Given
        String stockId = "test-stock-123";
        Long increaseQuantity = 50L;
        Stock increasedStock = Stock.register("test-product", 150L); // 증가 후 수량 (100 + 50)
        when(stockRepository.increaseStock(stockId, increaseQuantity)).thenReturn(1);
        when(stockRepository.findById(stockId)).thenReturn(Optional.of(increasedStock));

        // When
        Long result = stockService.increaseQuantityV1(stockId, increaseQuantity);

        // Then
        assertThat(result).isEqualTo(150L); // 100 + 50
    }

    @Test
    @DisplayName("increaseQuantityV1 - 재고 ID가 null인 경우")
    void increaseQuantityV1_NullStockId() {
        // Given
        String stockId = null;
        Long increaseQuantity = 50L;

        // When & Then
        assertThatThrownBy(() -> stockService.increaseQuantityV1(stockId, increaseQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고 ID는 필수입니다.");
    }

    @Test
    @DisplayName("increaseQuantityV1 - 재고 ID가 빈 문자열인 경우")
    void increaseQuantityV1_EmptyStockId() {
        // Given
        String stockId = "";
        Long increaseQuantity = 50L;

        // When & Then
        assertThatThrownBy(() -> stockService.increaseQuantityV1(stockId, increaseQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고 ID는 필수입니다.");
    }

    @Test
    @DisplayName("increaseQuantityV1 - 수량이 null인 경우")
    void increaseQuantityV1_NullQuantity() {
        // Given
        String stockId = "test-stock-123";
        Long increaseQuantity = null;

        // When & Then
        assertThatThrownBy(() -> stockService.increaseQuantityV1(stockId, increaseQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("수량은 0 이상이어야 합니다.");
    }

    @Test
    @DisplayName("increaseQuantityV1 - 수량이 음수인 경우")
    void increaseQuantityV1_NegativeQuantity() {
        // Given
        String stockId = "test-stock-123";
        Long increaseQuantity = -10L;

        // When & Then
        assertThatThrownBy(() -> stockService.increaseQuantityV1(stockId, increaseQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("수량은 0 이상이어야 합니다.");
    }

    @Test
    @DisplayName("increaseQuantityV1 - 재고가 존재하지 않는 경우")
    void increaseQuantityV1_StockNotFound() {
        // Given
        String stockId = "non-existent-stock";
        Long increaseQuantity = 50L;
        when(stockRepository.increaseStock(stockId, increaseQuantity)).thenReturn(0);

        // When & Then
        assertThatThrownBy(() -> stockService.increaseQuantityV1(stockId, increaseQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고를 찾을 수 없습니다: " + stockId);
    }

    @Test
    @DisplayName("decreaseQuantityV1 - 성공")
    void decreaseQuantityV1_Success() {
        // Given
        String stockId = "test-stock-123";
        Long decreaseQuantity = 30L;
        Stock decreasedStock = Stock.register("test-product", 70L); // 감소 후 수량 (100 - 30)
        when(stockRepository.decreaseStock(stockId, decreaseQuantity)).thenReturn(1);
        when(stockRepository.findById(stockId)).thenReturn(Optional.of(decreasedStock));

        // When
        Long result = stockService.decreaseQuantityV1(stockId, decreaseQuantity);

        // Then
        assertThat(result).isEqualTo(70L); // 100 - 30
    }

    @Test
    @DisplayName("decreaseQuantityV1 - 재고 ID가 null인 경우")
    void decreaseQuantityV1_NullStockId() {
        // Given
        String stockId = null;
        Long decreaseQuantity = 30L;

        // When & Then
        assertThatThrownBy(() -> stockService.decreaseQuantityV1(stockId, decreaseQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고 ID는 필수입니다.");
    }

    @Test
    @DisplayName("decreaseQuantityV1 - 재고 ID가 빈 문자열인 경우")
    void decreaseQuantityV1_EmptyStockId() {
        // Given
        String stockId = "";
        Long decreaseQuantity = 30L;

        // When & Then
        assertThatThrownBy(() -> stockService.decreaseQuantityV1(stockId, decreaseQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고 ID는 필수입니다.");
    }

    @Test
    @DisplayName("decreaseQuantityV1 - 수량이 null인 경우")
    void decreaseQuantityV1_NullQuantity() {
        // Given
        String stockId = "test-stock-123";
        Long decreaseQuantity = null;

        // When & Then
        assertThatThrownBy(() -> stockService.decreaseQuantityV1(stockId, decreaseQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("수량은 0 이상이어야 합니다.");
    }

    @Test
    @DisplayName("decreaseQuantityV1 - 수량이 음수인 경우")
    void decreaseQuantityV1_NegativeQuantity() {
        // Given
        String stockId = "test-stock-123";
        Long decreaseQuantity = -10L;

        // When & Then
        assertThatThrownBy(() -> stockService.decreaseQuantityV1(stockId, decreaseQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("수량은 0 이상이어야 합니다.");
    }

    @Test
    @DisplayName("decreaseQuantityV1 - 재고가 존재하지 않는 경우")
    void decreaseQuantityV1_StockNotFound() {
        // Given
        String stockId = "non-existent-stock";
        Long decreaseQuantity = 30L;
        when(stockRepository.decreaseStock(stockId, decreaseQuantity)).thenReturn(0);
        when(stockRepository.findById(stockId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> stockService.decreaseQuantityV1(stockId, decreaseQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고를 찾을 수 없습니다: " + stockId);
    }

    @Test
    @DisplayName("decreaseQuantityV1 - 재고 수량이 부족한 경우")
    void decreaseQuantityV1_InsufficientQuantity() {
        // Given
        String stockId = "test-stock-123";
        Long decreaseQuantity = 100L;
        Stock existingStock = Stock.register("test-product", 50L);
        
        when(stockRepository.decreaseStock(stockId, decreaseQuantity)).thenReturn(0);
        when(stockRepository.findById(stockId)).thenReturn(Optional.of(existingStock));

        // When & Then
        assertThatThrownBy(() -> stockService.decreaseQuantityV1(stockId, decreaseQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고 수량이 부족합니다. stockId: " + stockId);
    }

    @Test
    @DisplayName("decreaseQuantityV1 - 0으로 감소")
    void decreaseQuantityV1_ZeroQuantity() {
        // Given
        String stockId = "test-stock-123";
        Long decreaseQuantity = 0L;
        Stock unchangedStock = Stock.register("test-product", 100L); // 변경되지 않은 수량 (100 - 0)
        when(stockRepository.decreaseStock(stockId, decreaseQuantity)).thenReturn(1);
        when(stockRepository.findById(stockId)).thenReturn(Optional.of(unchangedStock));

        // When
        Long result = stockService.decreaseQuantityV1(stockId, decreaseQuantity);

        // Then
        assertThat(result).isEqualTo(100L); // 100 - 0
    }

    @Test
    @DisplayName("increaseQuantityV1과 decreaseQuantityV1 연속 실행")
    void increaseQuantityV1AndDecreaseQuantityV1_Sequential() {
        // Given
        String stockId = "test-stock-123";
        Long increaseQuantity = 100L;
        Long decreaseQuantity = 30L;
        
        Stock increasedStock = Stock.register("test-product", 200L); // 증가 후 수량 (100 + 100)
        Stock decreasedStock = Stock.register("test-product", 170L); // 감소 후 수량 (200 - 30)
        
        when(stockRepository.increaseStock(stockId, increaseQuantity)).thenReturn(1);
        when(stockRepository.decreaseStock(stockId, decreaseQuantity)).thenReturn(1);
        when(stockRepository.findById(stockId))
                .thenReturn(Optional.of(increasedStock))  // increaseQuantityV1에서 사용
                .thenReturn(Optional.of(decreasedStock)); // decreaseQuantityV1에서 사용

        // When
        Long increaseResult = stockService.increaseQuantityV1(stockId, increaseQuantity);
        Long decreaseResult = stockService.decreaseQuantityV1(stockId, decreaseQuantity);

        // Then
        assertThat(increaseResult).isEqualTo(200L); // 100 + 100
        assertThat(decreaseResult).isEqualTo(170L); // 200 - 30
    }
} 