package org.icd4.commerce.application.provided;

import org.icd4.commerce.application.required.StockRepository;
import org.icd4.commerce.domain.Stock;
import org.icd4.commerce.domain.StockStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@org.springframework.context.annotation.Import(org.icd4.commerce.config.TestConfig.class)
@Transactional
class StockFinderTest {

    @Autowired
    private StockFinder stockFinder;

    @Autowired
    private StockRepository stockRepository;

    @Test
    @DisplayName("재고 수량 조회 - 성공")
    void checkQuantity_Success() {
        // Given
        Stock savedStock = stockRepository.save(Stock.register("test-product-123", 150L));

        // When
        Long quantity = stockFinder.checkQuantity(savedStock.getId());

        // Then
        assertThat(quantity).isEqualTo(150L);
    }

    @Test
    @DisplayName("재고 수량 조회 - 실패 (존재하지 않는 재고)")
    void checkQuantity_Fail_NotFound() {
        // Given
        String nonExistentStockId = "non-existent-stock-id2";

        // When & Then
        assertThatThrownBy(() -> stockFinder.checkQuantity(nonExistentStockId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("재고를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("재고 전체 정보 조회 - 성공")
    void getStock_Success() {
        // Given
        String productId = "test-product-456";
        Long quantity = 200L;
        Stock savedStock = stockRepository.save(Stock.register(productId, quantity));

        // When
        Stock foundStock = stockFinder.getStock(savedStock.getId());

        // Then
        assertThat(foundStock).isNotNull();
        assertThat(foundStock.getId()).isEqualTo(savedStock.getId());
        assertThat(foundStock.getProductId()).isEqualTo(productId);
        assertThat(foundStock.getQuantity()).isEqualTo(quantity);
        assertThat(foundStock.getStockStatus()).isEqualTo(StockStatus.AVAILABLE);
        assertThat(foundStock.getCreatedAt()).isEqualTo(savedStock.getCreatedAt());
        assertThat(foundStock.getUpdatedAt()).isEqualTo(savedStock.getUpdatedAt());
    }

    @Test
    @DisplayName("재고 전체 정보 조회 - 실패 (존재하지 않는 재고)")
    void getStock_Fail_NotFound() {
        // Given
        String nonExistentStockId = "non-existent-stock-id";

        // When & Then
        assertThatThrownBy(() -> stockFinder.getStock(nonExistentStockId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("재고를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("수량이 0인 재고 조회")
    void checkQuantity_ZeroStock() {
        // Given
        Stock stock = Stock.register("test-product-zero", 100L);
        stock.empty(); // 재고를 0으로 만듦
        Stock savedStock = stockRepository.save(stock);

        // When
        Long quantity = stockFinder.checkQuantity(savedStock.getId());

        // Then
        assertThat(quantity).isEqualTo(0L);
    }

    @Test
    @DisplayName("품절 상태 재고 전체 정보 조회")
    void getStock_OutOfStock() {
        // Given
        Stock stock = Stock.register("test-product-out", 50L);
        stock.empty(); // 재고를 0으로 만들고 상태를 OUT_OF_STOCK으로 변경
        Stock savedStock = stockRepository.save(stock);

        // When
        Stock foundStock = stockFinder.getStock(savedStock.getId());

        // Then
        assertThat(foundStock.getQuantity()).isEqualTo(0L);
        assertThat(foundStock.getStockStatus()).isEqualTo(StockStatus.OUT_OF_STOCK);
    }

    @Test
    @DisplayName("대량 재고 수량 조회")
    void checkQuantity_LargeStock() {
        // Given
        Long largeQuantity = 1_000_000L;
        Stock savedStock = stockRepository.save(Stock.register("test-product-large", largeQuantity));

        // When
        Long quantity = stockFinder.checkQuantity(savedStock.getId());

        // Then
        assertThat(quantity).isEqualTo(largeQuantity);
    }

    @Test
    @DisplayName("여러 재고 동시 조회")
    void checkMultipleStocks() {
        // Given
        Stock stock1 = stockRepository.save(Stock.register("product-1", 100L));
        Stock stock2 = stockRepository.save(Stock.register("product-2", 200L));
        Stock stock3 = stockRepository.save(Stock.register("product-3", 300L));

        // When
        Long quantity1 = stockFinder.checkQuantity(stock1.getId());
        Long quantity2 = stockFinder.checkQuantity(stock2.getId());
        Long quantity3 = stockFinder.checkQuantity(stock3.getId());

        // Then
        assertThat(quantity1).isEqualTo(100L);
        assertThat(quantity2).isEqualTo(200L);
        assertThat(quantity3).isEqualTo(300L);
    }

    @Test
    @DisplayName("재고 수정 후 조회 확인")
    void checkQuantity_AfterModification() {
        // Given
        Stock stock = stockRepository.save(Stock.register("test-product-modify", 100L));
        
        // 수량 증가
        stock.increaseQuantity(50L);
        stockRepository.save(stock);

        // When
        Long quantity = stockFinder.checkQuantity(stock.getId());

        // Then
        assertThat(quantity).isEqualTo(150L); // 100 + 50
    }

    @Test
    @DisplayName("특수 문자를 포함한 상품 ID의 재고 조회")
    void checkQuantity_SpecialCharacterProductId() {
        // Given
        String specialProductId = "test-product-!@#$%^&*()_+";
        Stock savedStock = stockRepository.save(Stock.register(specialProductId, 75L));

        // When
        Stock foundStock = stockFinder.getStock(savedStock.getId());

        // Then
        assertThat(foundStock.getProductId()).isEqualTo(specialProductId);
        assertThat(foundStock.getQuantity()).isEqualTo(75L);
    }

    @Test
    @DisplayName("UUID 형태의 재고 ID로 조회")
    void checkQuantity_ValidUUIDFormat() {
        // Given
        Stock savedStock = stockRepository.save(Stock.register("test-product-uuid", 250L));

        // When
        Long quantity = stockFinder.checkQuantity(savedStock.getId());

        // Then
        assertThat(quantity).isEqualTo(250L);
        assertThat(savedStock.getId()).matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    }
} 