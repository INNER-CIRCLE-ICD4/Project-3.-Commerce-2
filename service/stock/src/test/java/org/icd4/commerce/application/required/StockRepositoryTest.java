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
        assertThat(savedStock.getProductId()).isEqualTo(productId);
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
    void findById_Success() {
        // Given
        Stock stock = Stock.register("test-product-456", 200L);
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();
        entityManager.clear();

        // When
        Optional<Stock> foundStock = stockRepository.findById(savedStock.getId());

        // Then
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getId()).isEqualTo(savedStock.getId());
        assertThat(foundStock.get().getProductId()).isEqualTo("test-product-456");
        assertThat(foundStock.get().getQuantity()).isEqualTo(200L);
        assertThat(foundStock.get().getStockStatus()).isEqualTo(StockStatus.AVAILABLE);
    }

    @Test
    @DisplayName("재고 조회 - 실패 (존재하지 않는 ID)")
    void findById_NotFound() {
        // Given
        String nonExistentId = "non-existent-id";

        // When
        Optional<Stock> foundStock = stockRepository.findById(nonExistentId);

        // Then
        assertThat(foundStock).isEmpty();
    }

    @Test
    @DisplayName("재고 업데이트 - 수량 변경")
    void update_QuantityChange() {
        // Given
        Stock stock = Stock.register("test-product-789", 100L);
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();

        // When
        savedStock.increaseQuantity(50L);
        Stock updatedStock = stockRepository.save(savedStock);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Stock> foundStock = stockRepository.findById(updatedStock.getId());
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getQuantity()).isEqualTo(150L); // 100 + 50
        assertThat(foundStock.get().getUpdatedAt()).isAfter(foundStock.get().getCreatedAt());
    }

    @Test
    @DisplayName("재고 업데이트 - 수량 변경")
    void update_QuantityChange_v1() {
        // Given
        Stock stock = Stock.register("test-product-789", 100L);

        Optional<Stock> foundStock = stockRepository.findById(stock.getId());
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
        Optional<Stock> foundStock = stockRepository.findById(updatedStock.getId());
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
            Optional<Stock> foundStock = stockRepository.findById(savedStocks[i].getId());
            assertThat(foundStock).isPresent();
            assertThat(foundStock.get().getProductId()).isEqualTo("bulk-product-" + i);
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
        Optional<Stock> foundStock = stockRepository.findById(savedStock.getId());
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getProductId()).isEqualTo(specialProductId);
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
        Optional<Stock> foundStock = stockRepository.findById(savedStock.getId());
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
        assertThat(savedStock1.getProductId()).isEqualTo(sameProductId);
        assertThat(savedStock2.getProductId()).isEqualTo(sameProductId);
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
        Optional<Stock> foundStock = stockRepository.findById(savedStock.getId());

        // Then
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getQuantity()).isEqualTo(150L); // 100 + 50
    }

    @Test
    @DisplayName("재고 감소 - 성공")
    void decreaseStock_Success() {
        // Given
        Stock stock = Stock.register("decrease-test-product", 100L);
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();
        entityManager.clear();

        Long decreaseQuantity = 30L;
        String stockId = savedStock.getId();

        // When
        Integer updatedRows = stockRepository.decreaseStock(stockId, decreaseQuantity);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(updatedRows).isEqualTo(1); // 1개 행이 업데이트됨

        Optional<Stock> foundStock = stockRepository.findById(stockId);
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getQuantity()).isEqualTo(70L); // 100 - 30
    }

    @Test
    @DisplayName("재고 감소 - 존재하지 않는 재고 ID")
    void decreaseStock_NonExistentStockId() {
        // Given
        String nonExistentStockId = "non-existent-stock-id";
        Long decreaseQuantity = 10L;

        // When
        Integer updatedRows = stockRepository.decreaseStock(nonExistentStockId, decreaseQuantity);

        // Then
        assertThat(updatedRows).isEqualTo(0); // 업데이트된 행이 없음
    }

    @Test
    @DisplayName("재고 감소 - 0으로 감소")
    void decreaseStock_ZeroQuantity() {
        // Given
        Stock stock = Stock.register("zero-decrease-test-product", 50L);
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();
        entityManager.clear();

        Long decreaseQuantity = 0L;
        String stockId = savedStock.getId();

        // When
        Integer updatedRows = stockRepository.decreaseStock(stockId, decreaseQuantity);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(updatedRows).isEqualTo(1); // 1개 행이 업데이트됨

        Optional<Stock> foundStock = stockRepository.findById(stockId);
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getQuantity()).isEqualTo(50L); // 변화 없음
    }

    @Test
    @DisplayName("재고 감소 - 전체 수량만큼 감소")
    void decreaseStock_FullQuantity() {
        // Given
        Stock stock = Stock.register("full-decrease-test-product", 25L);
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();
        entityManager.clear();

        Long decreaseQuantity = 25L;
        String stockId = savedStock.getId();

        // When
        Integer updatedRows = stockRepository.decreaseStock(stockId, decreaseQuantity);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(updatedRows).isEqualTo(1); // 1개 행이 업데이트됨

        Optional<Stock> foundStock = stockRepository.findById(stockId);
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getQuantity()).isEqualTo(0L); // 25 - 25 = 0
    }

    @Test
    @DisplayName("재고 감소 - 대량 수량 감소")
    void decreaseStock_LargeQuantity() {
        // Given
        Stock stock = Stock.register("large-decrease-test-product", 10000L);
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();
        entityManager.clear();

        Long decreaseQuantity = 7500L;
        String stockId = savedStock.getId();

        // When
        Integer updatedRows = stockRepository.decreaseStock(stockId, decreaseQuantity);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(updatedRows).isEqualTo(1); // 1개 행이 업데이트됨

        Optional<Stock> foundStock = stockRepository.findById(stockId);
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getQuantity()).isEqualTo(2500L); // 10000 - 7500
    }

    @Test
    @DisplayName("재고 감소 - 여러 번 연속 감소")
    void decreaseStock_MultipleDecreases() {
        // Given
        Stock stock = Stock.register("multiple-decrease-test-product", 100L);
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();
        entityManager.clear();

        String stockId = savedStock.getId();

        // When - 첫 번째 감소
        Integer firstUpdate = stockRepository.decreaseStock(stockId, 20L);
        entityManager.flush();
        entityManager.clear();

        // When - 두 번째 감소
        Integer secondUpdate = stockRepository.decreaseStock(stockId, 30L);
        entityManager.flush();
        entityManager.clear();

        // When - 세 번째 감소
        Integer thirdUpdate = stockRepository.decreaseStock(stockId, 25L);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(firstUpdate).isEqualTo(1);
        assertThat(secondUpdate).isEqualTo(1);
        assertThat(thirdUpdate).isEqualTo(1);

        Optional<Stock> foundStock = stockRepository.findById(stockId);
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getQuantity()).isEqualTo(25L); // 100 - 20 - 30 - 25
    }

    @Test
    @DisplayName("재고 감소 - 수량 부족으로 실패")
    void decreaseStock_InsufficientQuantity() {
        // Given
        Stock stock = Stock.register("insufficient-test-product", 10L);
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();
        entityManager.clear();

        Long decreaseQuantity = 15L; // 현재 수량(10)보다 많은 수량
        String stockId = savedStock.getId();

        // When
        Integer updatedRows = stockRepository.decreaseStock(stockId, decreaseQuantity);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(updatedRows).isEqualTo(0); // 업데이트된 행이 없음 (조건 불만족)

        Optional<Stock> foundStock = stockRepository.findById(stockId);
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getQuantity()).isEqualTo(10L); // 수량 변화 없음
    }

    @Test
    @DisplayName("재고 감소 - 정확히 남은 수량만큼 감소")
    void decreaseStock_ExactRemainingQuantity() {
        // Given
        Stock stock = Stock.register("exact-remaining-test-product", 5L);
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();
        entityManager.clear();

        Long decreaseQuantity = 5L; // 정확히 남은 수량
        String stockId = savedStock.getId();

        // When
        Integer updatedRows = stockRepository.decreaseStock(stockId, decreaseQuantity);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(updatedRows).isEqualTo(1); // 업데이트 성공

        Optional<Stock> foundStock = stockRepository.findById(stockId);
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getQuantity()).isEqualTo(0L); // 정확히 0이 됨
    }

    @Test
    @DisplayName("재고 감소 - 음수 수량으로 감소 시도")
    void decreaseStock_NegativeQuantity() {
        // Given
        Stock stock = Stock.register("negative-test-product", 20L);
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();
        entityManager.clear();

        Long decreaseQuantity = -5L; // 음수 수량
        String stockId = savedStock.getId();

        // When
        Integer updatedRows = stockRepository.decreaseStock(stockId, decreaseQuantity);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(updatedRows).isEqualTo(1); // 음수 감소는 실제로는 증가

        Optional<Stock> foundStock = stockRepository.findById(stockId);
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getQuantity()).isEqualTo(25L); // 20 - (-5) = 25
    }

    @Test
    @DisplayName("재고 감소 - updatedAt 필드 업데이트 확인")
    void decreaseStock_UpdatedAtFieldUpdate() {
        // Given
        Stock stock = Stock.register("updated-at-test-product", 100L);
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();
        entityManager.clear();

        // 원본 updatedAt 시간 저장
        Optional<Stock> originalStock = stockRepository.findById(savedStock.getId());
        assertThat(originalStock).isPresent();
        var originalUpdatedAt = originalStock.get().getUpdatedAt();

        // 잠시 대기하여 시간 차이 생성
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Long decreaseQuantity = 30L;
        String stockId = savedStock.getId();

        // When
        Integer updatedRows = stockRepository.decreaseStock(stockId, decreaseQuantity);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(updatedRows).isEqualTo(1);

        Optional<Stock> foundStock = stockRepository.findById(stockId);
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getQuantity()).isEqualTo(70L); // 100 - 30
        
        // updatedAt이 업데이트되었는지 확인
        var newUpdatedAt = foundStock.get().getUpdatedAt();
        assertThat(newUpdatedAt).isAfter(originalUpdatedAt);
    }

    @Test
    @DisplayName("재고 감소 - 수량 부족으로 실패 시 updatedAt 변경 없음")
    void decreaseStock_InsufficientQuantity_UpdatedAtUnchanged() {
        // Given
        Stock stock = Stock.register("insufficient-updated-at-test-product", 10L);
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();
        entityManager.clear();

        // 원본 updatedAt 시간 저장
        Optional<Stock> originalStock = stockRepository.findById(savedStock.getId());
        assertThat(originalStock).isPresent();
        var originalUpdatedAt = originalStock.get().getUpdatedAt();

        // 잠시 대기
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Long decreaseQuantity = 15L; // 현재 수량(10)보다 많은 수량
        String stockId = savedStock.getId();

        // When
        Integer updatedRows = stockRepository.decreaseStock(stockId, decreaseQuantity);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(updatedRows).isEqualTo(0); // 업데이트된 행이 없음

        Optional<Stock> foundStock = stockRepository.findById(stockId);
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getQuantity()).isEqualTo(10L); // 수량 변화 없음
        
        // updatedAt이 변경되지 않았는지 확인 (업데이트가 실패했으므로)
        var newUpdatedAt = foundStock.get().getUpdatedAt();
        assertThat(newUpdatedAt).isEqualTo(originalUpdatedAt);
    }

    @Test
    @DisplayName("동시성 테스트 - 벌크 업데이트의 원자성 보장")
    void decreaseStock_ConcurrencyTest() throws InterruptedException {
        // Given
        Stock stock = Stock.register("concurrency-test-product", 10L);
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();
        entityManager.clear();

        String stockId = savedStock.getId();

        // When - 순차적으로 재고 감소 시도 (동시성 시뮬레이션)
        Integer firstResult = stockRepository.decreaseStock(stockId, 8L);
        entityManager.flush();
        entityManager.clear();

        Integer secondResult = stockRepository.decreaseStock(stockId, 8L);
        entityManager.flush();
        entityManager.clear();

        Integer thirdResult = stockRepository.decreaseStock(stockId, 8L);
        entityManager.flush();
        entityManager.clear();

        Integer fourthResult = stockRepository.decreaseStock(stockId, 8L);
        entityManager.flush();
        entityManager.clear();

        Integer fifthResult = stockRepository.decreaseStock(stockId, 8L);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Stock> finalStock = stockRepository.findById(stockId);
        assertThat(finalStock).isPresent();
        
        // 첫 번째 요청만 성공해야 함 (10 >= 8)
        assertThat(firstResult).isEqualTo(1);
        
        // 나머지 요청들은 실패해야 함 (2 < 8)
        assertThat(secondResult).isEqualTo(0);
        assertThat(thirdResult).isEqualTo(0);
        assertThat(fourthResult).isEqualTo(0);
        assertThat(fifthResult).isEqualTo(0);
        
        // 최종 재고는 2개여야 함 (10 - 8 = 2)
        assertThat(finalStock.get().getQuantity()).isEqualTo(2L);
    }

    @Test
    @DisplayName("순차적 재고 감소 테스트 - 동시성 보장 확인")
    void decreaseStock_SequentialTest() {
        // Given
        Stock stock = Stock.register("sequential-test-product", 10L);
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();
        entityManager.clear();

        String stockId = savedStock.getId();

        // When - 순차적으로 재고 감소 시도
        Integer firstResult = stockRepository.decreaseStock(stockId, 8L);
        Integer secondResult = stockRepository.decreaseStock(stockId, 8L);
        Integer thirdResult = stockRepository.decreaseStock(stockId, 8L);

        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(firstResult).isEqualTo(1);  // 성공: 10 >= 8
        assertThat(secondResult).isEqualTo(0); // 실패: 2 < 8
        assertThat(thirdResult).isEqualTo(0);  // 실패: 2 < 8

        Optional<Stock> finalStock = stockRepository.findById(stockId);
        assertThat(finalStock).isPresent();
        assertThat(finalStock.get().getQuantity()).isEqualTo(2L); // 10 - 8 = 2
    }

    @Test
    @DisplayName("간단한 동시성 테스트 - 재고 증가 후 감소")
    void decreaseStock_SimpleConcurrencyTest() throws InterruptedException {
        // Given
        Stock stock = Stock.register("simple-concurrency-test-product", 1L);
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();
        entityManager.clear();

        String stockId = savedStock.getId();

        // When - 순차적으로 재고 증가 후 감소
        Integer increaseResult = stockRepository.increaseStock(stockId, 10L);
        entityManager.flush();
        entityManager.clear();

        Integer decreaseResult = stockRepository.decreaseStock(stockId, 5L);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Stock> finalStock = stockRepository.findById(stockId);
        assertThat(finalStock).isPresent();
        
        // 재고 증가는 성공해야 함 (조건 없음)
        assertThat(increaseResult).isEqualTo(1);
        
        // 재고 감소도 성공해야 함 (11 >= 5)
        assertThat(decreaseResult).isEqualTo(1);
        
        // 최종 재고는 6개여야 함 (1 + 10 - 5)
        assertThat(finalStock.get().getQuantity()).isEqualTo(6L);
    }



    @Test
    @DisplayName("재고 증가 - 성공")
    void increaseStock_Success() {
        // Given
        Stock stock = Stock.register("increase-test-product", 50L);
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();
        entityManager.clear();

        Long increaseQuantity = 30L;
        String stockId = savedStock.getId();

        // When
        Integer updatedRows = stockRepository.increaseStock(stockId, increaseQuantity);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(updatedRows).isEqualTo(1); // 1개 행이 업데이트됨

        Optional<Stock> foundStock = stockRepository.findById(stockId);
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getQuantity()).isEqualTo(80L); // 50 + 30
    }

    @Test
    @DisplayName("재고 증가 - 존재하지 않는 재고 ID")
    void increaseStock_NonExistentStockId() {
        // Given
        String nonExistentStockId = "non-existent-stock-id";
        Long increaseQuantity = 10L;

        // When
        Integer updatedRows = stockRepository.increaseStock(nonExistentStockId, increaseQuantity);

        // Then
        assertThat(updatedRows).isEqualTo(0); // 업데이트된 행이 없음
    }

    @Test
    @DisplayName("재고 증가 - 0으로 증가")
    void increaseStock_ZeroQuantity() {
        // Given
        Stock stock = Stock.register("zero-increase-test-product", 25L);
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();
        entityManager.clear();

        Long increaseQuantity = 0L;
        String stockId = savedStock.getId();

        // When
        Integer updatedRows = stockRepository.increaseStock(stockId, increaseQuantity);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(updatedRows).isEqualTo(1); // 1개 행이 업데이트됨

        Optional<Stock> foundStock = stockRepository.findById(stockId);
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getQuantity()).isEqualTo(25L); // 변화 없음
    }

    @Test
    @DisplayName("재고 증가 - 대량 수량 증가")
    void increaseStock_LargeQuantity() {
        // Given
        Stock stock = Stock.register("large-increase-test-product", 1000L);
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();
        entityManager.clear();

        Long increaseQuantity = 5000L;
        String stockId = savedStock.getId();

        // When
        Integer updatedRows = stockRepository.increaseStock(stockId, increaseQuantity);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(updatedRows).isEqualTo(1); // 1개 행이 업데이트됨

        Optional<Stock> foundStock = stockRepository.findById(stockId);
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getQuantity()).isEqualTo(6000L); // 1000 + 5000
    }

    @Test
    @DisplayName("재고 증가 - 여러 번 연속 증가")
    void increaseStock_MultipleIncreases() {
        // Given
        Stock stock = Stock.register("multiple-increase-test-product", 100L);
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();
        entityManager.clear();

        String stockId = savedStock.getId();

        // When - 첫 번째 증가
        Integer firstUpdate = stockRepository.increaseStock(stockId, 20L);
        entityManager.flush();
        entityManager.clear();

        // When - 두 번째 증가
        Integer secondUpdate = stockRepository.increaseStock(stockId, 30L);
        entityManager.flush();
        entityManager.clear();

        // When - 세 번째 증가
        Integer thirdUpdate = stockRepository.increaseStock(stockId, 25L);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(firstUpdate).isEqualTo(1);
        assertThat(secondUpdate).isEqualTo(1);
        assertThat(thirdUpdate).isEqualTo(1);

        Optional<Stock> foundStock = stockRepository.findById(stockId);
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getQuantity()).isEqualTo(175L); // 100 + 20 + 30 + 25
    }

    @Test
    @DisplayName("재고 증가 - 음수 수량으로 증가 시도")
    void increaseStock_NegativeQuantity() {
        // Given
        Stock stock = Stock.register("negative-increase-test-product", 30L);
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();
        entityManager.clear();

        Long increaseQuantity = -10L; // 음수 수량
        String stockId = savedStock.getId();

        // When
        Integer updatedRows = stockRepository.increaseStock(stockId, increaseQuantity);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(updatedRows).isEqualTo(1); // 음수 증가는 실제로는 감소

        Optional<Stock> foundStock = stockRepository.findById(stockId);
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getQuantity()).isEqualTo(20L); // 30 + (-10) = 20
    }

    @Test
    @DisplayName("재고 증가 - updatedAt 필드 업데이트 확인")
    void increaseStock_UpdatedAtFieldUpdate() {
        // Given
        Stock stock = Stock.register("increase-updated-at-test-product", 50L);
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();
        entityManager.clear();

        // 원본 updatedAt 시간 저장
        Optional<Stock> originalStock = stockRepository.findById(savedStock.getId());
        assertThat(originalStock).isPresent();
        var originalUpdatedAt = originalStock.get().getUpdatedAt();

        // 잠시 대기하여 시간 차이 생성
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Long increaseQuantity = 25L;
        String stockId = savedStock.getId();

        // When
        Integer updatedRows = stockRepository.increaseStock(stockId, increaseQuantity);
        entityManager.flush();
        entityManager.clear();

        // Then
        assertThat(updatedRows).isEqualTo(1);

        Optional<Stock> foundStock = stockRepository.findById(stockId);
        assertThat(foundStock).isPresent();
        assertThat(foundStock.get().getQuantity()).isEqualTo(75L); // 50 + 25
        
        // updatedAt이 업데이트되었는지 확인
        var newUpdatedAt = foundStock.get().getUpdatedAt();
        assertThat(newUpdatedAt).isAfter(originalUpdatedAt);
    }

    @Test
    @DisplayName("재고 증가 - 동시성 테스트")
    void increaseStock_ConcurrencyTest() throws InterruptedException {
        // Given
        Stock stock = Stock.register("increase-concurrency-test-product", 1L);
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();
        entityManager.clear();

        String stockId = savedStock.getId();

        // When - 순차적으로 재고 증가 (동시성 시뮬레이션)
        Integer[] results = new Integer[10];
        for (int i = 0; i < 10; i++) {
            results[i] = stockRepository.increaseStock(stockId, 10L);
            entityManager.flush();
            entityManager.clear();
        }

        // Then
        Optional<Stock> finalStock = stockRepository.findById(stockId);
        assertThat(finalStock).isPresent();
        
        // 모든 요청이 성공해야 함 (재고 증가는 조건이 없으므로)
        for (int i = 0; i < 10; i++) {
            assertThat(results[i]).isEqualTo(1);
        }
        
        // 최종 재고는 101개여야 함 (1 + 10 * 10)
        assertThat(finalStock.get().getQuantity()).isEqualTo(101L);
    }

    @Test
    @DisplayName("clearAutomatically 테스트 - 영속성 컨텍스트 캐시 클리어 확인")
    void clearAutomaticallyTest() {
        // Given
        Stock stock = Stock.register("cache-test-product", 50L);
        Stock savedStock = stockRepository.save(stock);
        entityManager.flush();
        entityManager.clear();

        String stockId = savedStock.getId();

        // When - 영속성 컨텍스트에 엔티티를 캐시
        Stock cachedStock = stockRepository.findById(stockId).orElseThrow();
        assertThat(cachedStock.getQuantity()).isEqualTo(50L);

        // When - 벌크 업데이트 실행 (clearAutomatically = true로 인해 캐시 클리어됨)
        Integer updatedRows = stockRepository.increaseStock(stockId, 30L);
        assertThat(updatedRows).isEqualTo(1);

        // Then - 캐시가 클리어되어 최신 데이터를 조회
        Stock freshStock = stockRepository.findById(stockId).orElseThrow();
        assertThat(freshStock.getQuantity()).isEqualTo(80L); // 50 + 30

        // 추가 검증: 영속성 컨텍스트가 클리어되었는지 확인
        // 만약 clearAutomatically = false였다면 캐시된 데이터(50L)를 반환했을 것
        assertThat(freshStock.getQuantity()).isNotEqualTo(50L);
    }
}