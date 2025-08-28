package org.icd4.commerce.application.required;

import jakarta.persistence.EntityManager;
import org.icd4.commerce.domain.Stock;
import org.icd4.commerce.domain.StockStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class StockRepositoryTest {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("재고 저장 - 성공")
    void save_Success() {
        // Given
        String productId = "test-product-123";
        Long quantity = 100L;
        Stock stock = Stock.register(productId, quantity);

        // When
        Stock savedStock = stockRepository.save(stock);

        // Then
        assertThat(savedStock).isNotNull();
        assertThat(savedStock.getId()).isNotNull();
        assertThat(savedStock.getSku()).isEqualTo(productId);
        assertThat(savedStock.getQuantity()).isEqualTo(quantity);
        assertThat(savedStock.getStockStatus()).isEqualTo(StockStatus.AVAILABLE);
        assertThat(savedStock.getCreatedAt()).isNotNull();
        assertThat(savedStock.getUpdatedAt()).isNotNull();

        // DB에서 flush하여 실제 저장 확인
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("재고 조회 - 성공")
    void findBySku_Success() {
        // Given
        Stock stock = Stock.register("test-product-456", 200L);
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();
        entityManager.clear();

        // When
        Optional<Stock> foundStock = stockRepository.findBySku(savedStock.getSku());

        // Then
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getSku()).isEqualTo(savedStock.getSku());
        assertThat(foundStock.get().getSku()).isEqualTo("test-product-456");
        assertThat(foundStock.get().getQuantity()).isEqualTo(200L);
        assertThat(foundStock.get().getStockStatus()).isEqualTo(StockStatus.AVAILABLE);
    }

    @Test
    @DisplayName("재고 조회 - 실패 (존재하지 않는 ID)")
    void findBySku_NotFound() {
        // Given
        String nonExistentId = "non-existent-id";

        // When
        Optional<Stock> foundStock = stockRepository.findBySku(nonExistentId);

        // Then
        assertThat(foundStock).isEmpty();
    }

    @Test
    @DisplayName("재고 업데이트 - 수량 변경")
    void update_QuantityChange() throws InterruptedException {
        // Given
        Stock stock = Stock.register("test-product-789", 100L);
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();

        // When
        sleep(1000);
        savedStock.increaseQuantity(50L);
        Stock updatedStock = stockRepository.save(savedStock);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Stock> foundStock = stockRepository.findBySku(updatedStock.getSku());
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getQuantity()).isEqualTo(150L); // 100 + 50
        assertThat(foundStock.get().getUpdatedAt()).isAfter(foundStock.get().getCreatedAt());
    }

    @Test
    @DisplayName("재고 업데이트 - 상태 변경")
    void update_StatusChange() {
        // Given
        Stock stock = Stock.register("test-product-status", 50L);
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();

        // When
        savedStock.empty(); // 재고를 0으로 만들고 상태를 OUT_OF_STOCK으로 변경
        Stock updatedStock = stockRepository.save(savedStock);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Stock> foundStock = stockRepository.findBySku(updatedStock.getSku());
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getQuantity()).isEqualTo(0L);
        assertThat(foundStock.get().getStockStatus()).isEqualTo(StockStatus.OUT_OF_STOCK);
    }

    @Test
    @DisplayName("대량 재고 저장 및 조회")
    void bulkOperations() {
        // Given
        Stock[] stocks = new Stock[5];
        for (int i = 0; i < 5; i++) {
            stocks[i] = Stock.register("bulk-product-" + i, (long) (i + 1) * 10);
        }

        // When - 대량 저장
        Stock[] savedStocks = new Stock[5];
        for (int i = 0; i < 5; i++) {
            savedStocks[i] = stockRepository.save(stocks[i]);
        }
        entityManager.flush();
        entityManager.clear();

        // Then - 대량 조회 및 검증
        for (int i = 0; i < 5; i++) {
            Optional<Stock> foundStock = stockRepository.findBySku(savedStocks[i].getSku());
            assertThat(foundStock).isPresent();
            assertThat(foundStock.get().getSku()).isEqualTo("bulk-product-" + i);
            assertThat(foundStock.get().getQuantity()).isEqualTo((long) (i + 1) * 10);
        }
    }

    @Test
    @DisplayName("특수 문자를 포함한 상품 ID 저장")
    void save_SpecialCharacterProductId() {
        // Given
        String specialProductId = "test-product-!@#$%^&*()_+-=[]{}|;':\",./<>?";
        Stock stock = Stock.register(specialProductId, 75L);

        // When
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Stock> foundStock = stockRepository.findBySku(savedStock.getSku());
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getSku()).isEqualTo(specialProductId);
    }

    @Test
    @DisplayName("최대 수량 저장")
    void save_MaxQuantity() {
        // Given
        Long maxQuantity = Long.MAX_VALUE;
        Stock stock = Stock.register("test-product-max", maxQuantity);

        // When
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Stock> foundStock = stockRepository.findBySku(savedStock.getSku());
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getQuantity()).isEqualTo(maxQuantity);
    }

    @Test
    @DisplayName("동일한 상품 ID로 여러 재고 저장 (다른 재고 ID)")
    void save_SameProductIdDifferentStockId() {
        // Given
        String sameProductId = "same-product-id";
        Stock stock1 = Stock.register(sameProductId, 100L);
        Stock stock2 = Stock.register(sameProductId, 200L);

        // When
        Stock savedStock1 = stockRepository.save(stock1);
        Stock savedStock2 = stockRepository.save(stock2);
        entityManager.flush();

        // Then
        assertThat(savedStock1.getId()).isNotEqualTo(savedStock2.getId());
        assertThat(savedStock1.getSku()).isEqualTo(sameProductId);
        assertThat(savedStock2.getSku()).isEqualTo(sameProductId);
        assertThat(savedStock1.getQuantity()).isEqualTo(100L);
        assertThat(savedStock2.getQuantity()).isEqualTo(200L);
    }

    @Test
    @DisplayName("UUID 형태의 ID 생성 확인")
    void save_UUIDFormat() {
        // Given
        Stock stock = Stock.register("uuid-test-product", 50L);

        // When
        Stock savedStock = stockRepository.save(stock);

        // Then
        String stockId = savedStock.getId();
        assertThat(stockId).isNotNull();
        assertThat(stockId).matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    }

    @Test
    @DisplayName("영속성 컨텍스트 테스트")
    void persistenceContextTest() {
        // Given
        Stock stock = Stock.register("persistence-test-product", 100L);
        Stock savedStock = stockRepository.save(stock);

        // When - 같은 트랜잭션 내에서 수정
        savedStock.increaseQuantity(50L);

        // entityManager flush 없이도 변경사항이 반영되어야 함
        Optional<Stock> foundStock = stockRepository.findBySku(savedStock.getSku());

        // Then
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getQuantity()).isEqualTo(150L); // 100 + 50
    }
}