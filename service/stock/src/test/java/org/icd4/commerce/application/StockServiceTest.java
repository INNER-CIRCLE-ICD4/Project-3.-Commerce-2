package org.icd4.commerce.application;

import org.icd4.commerce.application.provided.StockFinder;
import org.icd4.commerce.application.provided.StockRegister;
import org.icd4.commerce.application.required.StockRepository;
import org.icd4.commerce.domain.Stock;
import org.icd4.commerce.domain.StockStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class StockServiceTest {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @Test
    @DisplayName("StockService가 모든 포트 인터페이스를 구현하는지 확인")
    void implementsAllPortInterfaces() {
        // Given & When & Then
        assertThat(stockService).isInstanceOf(StockRegister.class);
        assertThat(stockService).isInstanceOf(StockFinder.class);
    }

    @Test
    @DisplayName("전체 워크플로우 테스트: 등록 → 조회 → 증가 → 감소")
    void fullWorkflowTest() {
        // Given
        String productId = "workflow-test-product";
        Long initialQuantity = 100L;

        // 1. 재고 등록
        Stock registeredStock = stockService.register(productId, initialQuantity);
        assertThat(registeredStock.getSku()).isEqualTo(productId);
        assertThat(registeredStock.getQuantity()).isEqualTo(initialQuantity);

        // 2. 재고 조회
        Stock foundStock = stockService.getStock(registeredStock.getId());
        assertThat(foundStock.getId()).isEqualTo(registeredStock.getId());
        assertThat(foundStock.getQuantity()).isEqualTo(initialQuantity);

        // 3. 재고 수량만 조회
        Long quantity = stockService.checkQuantity(registeredStock.getId());
        assertThat(quantity).isEqualTo(initialQuantity);

        // 4. 재고 증가
        Long increaseAmount = 50L;
        stockService.increaseQuantity(registeredStock.getId(), increaseAmount);
        
        Long increasedQuantity = stockService.checkQuantity(registeredStock.getId());
        assertThat(increasedQuantity).isEqualTo(150L); // 100 + 50

        // 5. 재고 감소
        Long decreaseAmount = 30L;
        stockService.decreaseQuantity(registeredStock.getId(), decreaseAmount);
        
        Long finalQuantity = stockService.checkQuantity(registeredStock.getId());
        assertThat(finalQuantity).isEqualTo(120L); // 150 - 30
    }

    @Test
    @DisplayName("동시성 시뮬레이션: 여러 재고 동시 처리")
    void concurrentStockHandling() {
        // Given
        Stock stock1 = stockService.register("concurrent-product-1", 100L);
        Stock stock2 = stockService.register("concurrent-product-2", 200L);
        Stock stock3 = stockService.register("concurrent-product-3", 300L);

        // When - 각각 다른 연산 수행
        stockService.increaseQuantity(stock1.getId(), 50L);
        stockService.decreaseQuantity(stock2.getId(), 50L);
        stockService.increaseQuantity(stock3.getId(), 100L);

        // Then
        assertThat(stockService.checkQuantity(stock1.getId())).isEqualTo(150L); // 100 + 50
        assertThat(stockService.checkQuantity(stock2.getId())).isEqualTo(150L); // 200 - 50
        assertThat(stockService.checkQuantity(stock3.getId())).isEqualTo(400L); // 300 + 100
    }

    @Test
    @DisplayName("트랜잭션 테스트: 하나의 트랜잭션에서 여러 작업")
    void transactionTest() {
        // Given
        Stock stock = stockService.register("transaction-test-product", 100L);
        String stockId = stock.getId();

        // When - 같은 트랜잭션에서 여러 작업
        stockService.increaseQuantity(stockId, 30L);
        stockService.decreaseQuantity(stockId, 20L);
        
        // Then
        Long finalQuantity = stockService.checkQuantity(stockId);
        assertThat(finalQuantity).isEqualTo(110L); // 100 + 30 - 20

        // DB에서 직접 확인
        Optional<Stock> dbStock = stockRepository.findBySku(stockId);
        assertThat(dbStock).isPresent();
        assertThat(dbStock.get().getQuantity()).isEqualTo(110L);
    }

    @Test
    @DisplayName("에러 처리 테스트: 다양한 예외 상황")
    void errorHandlingTest() {
        // 1. 존재하지 않는 재고 ID로 조회
        assertThatThrownBy(() -> stockService.getStock("non-existent-id"))
                .isInstanceOf(NoSuchElementException.class);

        assertThatThrownBy(() -> stockService.checkQuantity("non-existent-id"))
                .isInstanceOf(NoSuchElementException.class);

        // 2. 잘못된 값으로 등록
        assertThatThrownBy(() -> stockService.register(null, 100L))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> stockService.register("test-product", 0L))
                .isInstanceOf(IllegalArgumentException.class);

        // 3. 존재하지 않는 재고 ID로 수량 변경 (예외 발생)
        assertThatThrownBy(() -> stockService.increaseQuantity("non-existent-id", 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock not found");

        assertThatThrownBy(() -> stockService.decreaseQuantity("non-existent-id", 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock not found");
    }

    @Test
    @DisplayName("비즈니스 로직 테스트: 재고 감소 시 부족한 경우")
    void businessLogicTest_InsufficientStock() {
        // Given
        Stock stock = stockService.register("business-test-product", 50L);

        // When & Then - 재고 부족 시 도메인에서 예외 발생
        String stockId = stock.getId();
        
        // 정상 감소는 성공
        stockService.decreaseQuantity(stockId, 30L);
        assertThat(stockService.checkQuantity(stockId)).isEqualTo(20L);

        // 재고보다 많이 감소 시도 - 도메인에서 예외 발생
        assertThatThrownBy(() -> stockService.decreaseQuantity(stockId, 50L)) // 20개밖에 없는데 50개 감소 시도
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("현재 재고보다 많습니다");
        
        // 예외로 인해 재고는 변하지 않아야 함
        assertThat(stockService.checkQuantity(stockId)).isEqualTo(20L);
    }


    @Test
    @DisplayName("상태 관리 테스트: AVAILABLE과 OUT_OF_STOCK")
    void statusManagementTest() {
        // Given
        Stock stock = stockService.register("status-test-product", 100L);
        
        // 초기 상태는 AVAILABLE
        Stock foundStock = stockService.getStock(stock.getId());
        assertThat(foundStock.getStockStatus()).isEqualTo(StockStatus.AVAILABLE);
        
        // 재고를 0으로 만들어도 상태는 자동으로 변경되지 않음 (도메인 로직에 따라)
        stockService.decreaseQuantity(stock.getId(), 100L);
        Stock zeroStock = stockService.getStock(stock.getId());
        assertThat(zeroStock.getQuantity()).isEqualTo(0L);
        assertThat(zeroStock.getStockStatus()).isEqualTo(StockStatus.AVAILABLE); // 상태는 그대로
    }

    @Test
    @DisplayName("대량 데이터 처리 테스트")
    void bulkDataProcessingTest() {
        // Given - 여러 재고를 등록
        Stock[] stocks = new Stock[10];
        for (int i = 0; i < 10; i++) {
            stocks[i] = stockService.register("bulk-product-" + i, (long) (i + 1) * 10);
        }

        // When - 모든 재고에 대해 연산 수행
        for (int i = 0; i < 10; i++) {
            stockService.increaseQuantity(stocks[i].getId(), 5L);
        }

        // Then - 모든 재고 상태 확인
        for (int i = 0; i < 10; i++) {
            Long expectedQuantity = (long) (i + 1) * 10 + 5L;
            assertThat(stockService.checkQuantity(stocks[i].getId())).isEqualTo(expectedQuantity);
        }
    }

    @Test
    @DisplayName("경계값 테스트: 최소/최대 수량")
    void boundaryValueTest() {
        // 최소 수량 (1)
        Stock minStock = stockService.register("min-test-product", 1L);
        assertThat(stockService.checkQuantity(minStock.getId())).isEqualTo(1L);

        // 큰 수량
        Long largeQuantity = 1_000_000L;
        Stock maxStock = stockService.register("max-test-product", largeQuantity);
        assertThat(stockService.checkQuantity(maxStock.getId())).isEqualTo(largeQuantity);

        // 최소 단위 증가/감소
        stockService.increaseQuantity(minStock.getId(), 1L);
        assertThat(stockService.checkQuantity(minStock.getId())).isEqualTo(2L);

        stockService.decreaseQuantity(minStock.getId(), 1L);
        assertThat(stockService.checkQuantity(minStock.getId())).isEqualTo(1L);
    }
}