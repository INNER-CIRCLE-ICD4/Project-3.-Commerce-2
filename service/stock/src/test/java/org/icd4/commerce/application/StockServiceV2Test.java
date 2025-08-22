package org.icd4.commerce.application;

import org.icd4.commerce.adapter.persistence.StockRedisRepositoryAdapter;
import org.icd4.commerce.domain.Stock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockServiceV2Test {

    @Mock
    private StockRedisRepositoryAdapter stockRedisRepositoryAdapter;

    @InjectMocks
    private StockService stockService;

    @Test
    @DisplayName("increaseQuantityV2 - 성공")
    void increaseQuantityV2_Success() {
        // Given
        Long initialQuantity = 100L;
        Long increaseQuantity = 50L;
        Stock initialStock = Stock.register("test-product", initialQuantity);
        String stockId = initialStock.getId();
        Stock increasedStock = Stock.register("test-product", initialQuantity + increaseQuantity);
        when(stockRedisRepositoryAdapter.increaseStock(stockId, increaseQuantity)).thenReturn(1);
        when(stockRedisRepositoryAdapter.findById(stockId))
                .thenReturn(Optional.of(increasedStock));

        // When
        Long result = stockService.increaseQuantityV2(stockId, increaseQuantity);

        // Then
        assertThat(result).isEqualTo(150L); // 100 + 50
    }

    @Test
    @DisplayName("increaseQuantityV2 - 재고 ID가 null인 경우")
    void increaseQuantityV2_NullStockId() {
        // Given
        String stockId = null;
        Long increaseQuantity = 50L;

        // When & Then
        assertThatThrownBy(() -> stockService.increaseQuantityV2(stockId, increaseQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고 ID는 필수입니다.");
    }

    @Test
    @DisplayName("increaseQuantityV2 - 재고 ID가 빈 문자열인 경우")
    void increaseQuantityV2_EmptyStockId() {
        // Given
        String stockId = "";
        Long increaseQuantity = 50L;

        // When & Then
        assertThatThrownBy(() -> stockService.increaseQuantityV2(stockId, increaseQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고 ID는 필수입니다.");
    }

    @Test
    @DisplayName("increaseQuantityV2 - 수량이 null인 경우")
    void increaseQuantityV2_NullQuantity() {
        // Given
        String stockId = "test-stock-123";
        Long increaseQuantity = null;

        // When & Then
        assertThatThrownBy(() -> stockService.increaseQuantityV2(stockId, increaseQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("수량은 0 이상이어야 합니다.");
    }

    @Test
    @DisplayName("increaseQuantityV2 - 수량이 음수인 경우")
    void increaseQuantityV2_NegativeQuantity() {
        // Given
        String stockId = "test-stock-123";
        Long increaseQuantity = -10L;

        // When & Then
        assertThatThrownBy(() -> stockService.increaseQuantityV2(stockId, increaseQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("수량은 0 이상이어야 합니다.");
    }

    @Test
    @DisplayName("increaseQuantityV2 - 재고가 존재하지 않는 경우")
    void increaseQuantityV2_StockNotFound() {
        // Given
        String stockId = "non-existent-stock";
        Long increaseQuantity = 50L;
        when(stockRedisRepositoryAdapter.increaseStock(stockId, increaseQuantity)).thenReturn(0);

        // When & Then
        assertThatThrownBy(() -> stockService.increaseQuantityV2(stockId, increaseQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고를 찾을 수 없습니다: " + stockId);
    }

    @Test
    @DisplayName("decreaseQuantityV2 - 성공")
    void decreaseQuantityV2_Success() {
        // Given
        Long initialQuantity = 100L;
        Long decreaseQuantity = 30L;
        Stock initialStock = Stock.register("test-product", initialQuantity);
        String stockId = initialStock.getId();
        Stock decreasedStock = Stock.register("test-product", initialQuantity - decreaseQuantity);
        when(stockRedisRepositoryAdapter.decreaseStock(stockId, decreaseQuantity)).thenReturn(1);
        when(stockRedisRepositoryAdapter.findById(stockId))
                .thenReturn(Optional.of(decreasedStock));

        // When
        Long result = stockService.decreaseQuantityV2(stockId, decreaseQuantity);

        // Then
        assertThat(result).isEqualTo(70L); // 100 - 30
    }

    @Test
    @DisplayName("decreaseQuantityV2 - 재고 ID가 null인 경우")
    void decreaseQuantityV2_NullStockId() {
        // Given
        String stockId = null;
        Long decreaseQuantity = 30L;

        // When & Then
        assertThatThrownBy(() -> stockService.decreaseQuantityV2(stockId, decreaseQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고 ID는 필수입니다.");
    }

    @Test
    @DisplayName("decreaseQuantityV2 - 재고 ID가 빈 문자열인 경우")
    void decreaseQuantityV2_EmptyStockId() {
        // Given
        String stockId = "";
        Long decreaseQuantity = 30L;

        // When & Then
        assertThatThrownBy(() -> stockService.decreaseQuantityV2(stockId, decreaseQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고 ID는 필수입니다.");
    }

    @Test
    @DisplayName("decreaseQuantityV2 - 수량이 null인 경우")
    void decreaseQuantityV2_NullQuantity() {
        // Given
        String stockId = "test-stock-123";
        Long decreaseQuantity = null;

        // When & Then
        assertThatThrownBy(() -> stockService.decreaseQuantityV2(stockId, decreaseQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("수량은 0 이상이어야 합니다.");
    }

    @Test
    @DisplayName("decreaseQuantityV2 - 수량이 음수인 경우")
    void decreaseQuantityV2_NegativeQuantity() {
        // Given
        String stockId = "test-stock-123";
        Long decreaseQuantity = -10L;

        // When & Then
        assertThatThrownBy(() -> stockService.decreaseQuantityV2(stockId, decreaseQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("수량은 0 이상이어야 합니다.");
    }

    @Test
    @DisplayName("decreaseQuantityV2 - 재고가 존재하지 않는 경우")
    void decreaseQuantityV2_StockNotFound() {
        // Given
        String stockId = "non-existent-stock";
        Long decreaseQuantity = 30L;
        when(stockRedisRepositoryAdapter.decreaseStock(stockId, decreaseQuantity)).thenReturn(0);
        when(stockRedisRepositoryAdapter.findById(stockId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> stockService.decreaseQuantityV2(stockId, decreaseQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고를 찾을 수 없습니다: " + stockId);
    }

    @Test
    @DisplayName("decreaseQuantityV2 - 재고 수량이 부족한 경우")
    void decreaseQuantityV2_InsufficientQuantity() {
        // Given
        String stockId = "test-stock-123";
        Long decreaseQuantity = 100L;
        Stock existingStock = Stock.register("test-product", 50L);
        
        when(stockRedisRepositoryAdapter.decreaseStock(stockId, decreaseQuantity)).thenReturn(0);
        when(stockRedisRepositoryAdapter.findById(stockId)).thenReturn(Optional.of(existingStock));

        // When & Then
        assertThatThrownBy(() -> stockService.decreaseQuantityV2(stockId, decreaseQuantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고 수량이 부족합니다. stockId: " + stockId);
    }

    @Test
    @DisplayName("decreaseQuantityV2 - 0으로 감소")
    void decreaseQuantityV2_ZeroQuantity() {
        // Given
        Long initialQuantity = 100L;
        Long decreaseQuantity = 0L;
        Stock initialStock = Stock.register("test-product", initialQuantity);
        String stockId = initialStock.getId();
        Stock unchangedStock = Stock.register("test-product", initialQuantity);
        when(stockRedisRepositoryAdapter.decreaseStock(stockId, decreaseQuantity)).thenReturn(1);
        when(stockRedisRepositoryAdapter.findById(stockId)).thenReturn(Optional.of(unchangedStock));

        // When
        Long result = stockService.decreaseQuantityV2(stockId, decreaseQuantity);

        // Then
        assertThat(result).isEqualTo(100L); // 100 - 0
    }

    @Test
    @DisplayName("increaseQuantityV2과 decreaseQuantityV2 연속 실행")
    void increaseQuantityV2AndDecreaseQuantityV2_Sequential() {
        // Given
        Long initialQuantity = 100L;
        Long increaseQuantity = 100L;
        Long decreaseQuantity = 30L;
        
        Stock initialStock = Stock.register("test-product", initialQuantity);
        String stockId = initialStock.getId();
        Stock increasedStock = Stock.register("test-product", initialQuantity + increaseQuantity);
        Stock decreasedStock = Stock.register("test-product", initialQuantity + increaseQuantity - decreaseQuantity);
        
        when(stockRedisRepositoryAdapter.increaseStock(stockId, increaseQuantity)).thenReturn(1);
        when(stockRedisRepositoryAdapter.decreaseStock(stockId, decreaseQuantity)).thenReturn(1);
        when(stockRedisRepositoryAdapter.findById(stockId))
                .thenReturn(Optional.of(increasedStock))  // increaseQuantityV2에서 사용
                .thenReturn(Optional.of(decreasedStock)); // decreaseQuantityV2에서 사용

        // When
        Long increaseResult = stockService.increaseQuantityV2(stockId, increaseQuantity);
        Long decreaseResult = stockService.decreaseQuantityV2(stockId, decreaseQuantity);

        // Then
        assertThat(increaseResult).isEqualTo(200L); // 100 + 100
        assertThat(decreaseResult).isEqualTo(170L); // 200 - 30
    }
} 