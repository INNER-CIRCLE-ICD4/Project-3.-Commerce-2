package org.icd4.commerce.adapter.persistence;

import org.icd4.commerce.domain.Stock;
import org.icd4.commerce.domain.StockStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class StockRedisRepositoryAdapterIntegrationTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private StockRedisRepositoryAdapter stockRedisRepositoryAdapter;

    @Autowired
    private StockRedisRepository stockRedisRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private Stock testStock;

    @BeforeEach
    void setUp() {
        // Redis 데이터 초기화
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        testStock = Stock.register("test-product-123", 100L);
    }

    @Test
    @DisplayName("save - Redis에 재고 수량 저장")
    void save_ShouldSaveStockQuantityToRedis() {
        // When
        Stock savedStock = stockRedisRepositoryAdapter.save(testStock);

        // Then
        assertThat(savedStock).isEqualTo(testStock);
        
        // Redis에서 직접 확인
        String redisValue = redisTemplate.opsForValue().get("stock:" + testStock.getId());
        assertThat(redisValue).isEqualTo("100");
    }

    @Test
    @DisplayName("findById - Redis에서 재고 조회 성공")
    void findById_ShouldReturnStockFromRedis() {
        // Given
        String stockId = "test-stock-123";
        Long quantity = 50L;
        stockRedisRepository.setStock(stockId, quantity);

        // When
        Optional<Stock> foundStock = stockRedisRepositoryAdapter.findById(stockId);

        // Then
        assertThat(foundStock).isPresent();
        Stock stock = foundStock.get();
        assertThat(stock.getId()).isEqualTo(stockId);
        assertThat(stock.getQuantity()).isEqualTo(quantity);
        assertThat(stock.getProductId()).isEqualTo("unknown");
        assertThat(stock.getStockStatus()).isEqualTo(StockStatus.AVAILABLE);
    }

    @Test
    @DisplayName("findById - Redis에 재고가 없는 경우")
    void findById_ShouldReturnEmptyWhenStockNotFound() {
        // Given
        String stockId = "non-existent-stock";

        // When
        Optional<Stock> foundStock = stockRedisRepositoryAdapter.findById(stockId);

        // Then
        assertThat(foundStock).isEmpty();
    }

    @Test
    @DisplayName("increaseStock - Redis에서 재고 증가 성공")
    void increaseStock_ShouldIncreaseStockInRedis() {
        // Given
        String stockId = "test-stock-123";
        Long currentQuantity = 100L;
        Long increaseQuantity = 50L;
        stockRedisRepository.setStock(stockId, currentQuantity);

        // When
        int result = stockRedisRepositoryAdapter.increaseStock(stockId, increaseQuantity);

        // Then
        assertThat(result).isEqualTo(1);
        
        // Redis에서 직접 확인
        Long newQuantity = stockRedisRepository.getStock(stockId);
        assertThat(newQuantity).isEqualTo(currentQuantity + increaseQuantity);
    }

    @Test
    @DisplayName("decreaseStock - Redis에서 재고 감소 성공")
    void decreaseStock_ShouldDecreaseStockInRedis() {
        // Given
        String stockId = "test-stock-123";
        Long initialQuantity = 100L;
        Long decreaseQuantity = 30L;
        stockRedisRepository.setStock(stockId, initialQuantity);

        // When
        int result = stockRedisRepositoryAdapter.decreaseStock(stockId, decreaseQuantity);

        // Then
        assertThat(result).isEqualTo(1);
        
        // Redis에서 직접 확인
        Long newQuantity = stockRedisRepository.getStock(stockId);
        assertThat(newQuantity).isEqualTo(initialQuantity - decreaseQuantity);
    }

    @Test
    @DisplayName("decreaseStock - 재고 부족으로 인한 실패")
    void decreaseStock_ShouldFailWhenInsufficientStock() {
        // Given
        String stockId = "test-stock-123";
        Long initialQuantity = 100L;
        Long decreaseQuantity = 150L; // 현재 재고보다 많은 수량
        stockRedisRepository.setStock(stockId, initialQuantity);

        // When
        int result = stockRedisRepositoryAdapter.decreaseStock(stockId, decreaseQuantity);

        // Then
        assertThat(result).isEqualTo(0);
        
        // Redis에서 원래 수량이 유지되었는지 확인
        Long finalQuantity = stockRedisRepository.getStock(stockId);
        assertThat(finalQuantity).isEqualTo(initialQuantity);
    }

    @Test
    @DisplayName("Redis 원자적 연산 테스트 - 여러 번 감소")
    void decreaseStock_ShouldUseRedisAtomicOperation() {
        // Given
        String stockId = "test-stock-123";
        Long initialQuantity = 100L;
        stockRedisRepository.setStock(stockId, initialQuantity);

        // When
        int result1 = stockRedisRepositoryAdapter.decreaseStock(stockId, 30L);
        int result2 = stockRedisRepositoryAdapter.decreaseStock(stockId, 40L);
        int result3 = stockRedisRepositoryAdapter.decreaseStock(stockId, 20L);

        // Then
        assertThat(result1).isEqualTo(1);
        assertThat(result2).isEqualTo(1);
        assertThat(result3).isEqualTo(1);
        
        // Redis에서 최종 수량 확인
        Long finalQuantity = stockRedisRepository.getStock(stockId);
        assertThat(finalQuantity).isEqualTo(10L); // 100 - 30 - 40 - 20 = 10
    }

    @Test
    @DisplayName("Redis 원자적 연산 테스트 - 재고 부족 시 롤백")
    void decreaseStock_ShouldRollbackWhenInsufficientStock() {
        // Given
        String stockId = "test-stock-123";
        Long initialQuantity = 100L;
        stockRedisRepository.setStock(stockId, initialQuantity);

        // When
        int result1 = stockRedisRepositoryAdapter.decreaseStock(stockId, 60L);
        int result2 = stockRedisRepositoryAdapter.decreaseStock(stockId, 50L);

        // Then
        assertThat(result1).isEqualTo(1);
        assertThat(result2).isEqualTo(0);
        
        // Redis에서 첫 번째 감소만 적용되었는지 확인
        Long finalQuantity = stockRedisRepository.getStock(stockId);
        assertThat(finalQuantity).isEqualTo(40L); // 100 - 60 = 40 (두 번째 감소는 롤백됨)
    }

    @Test
    @DisplayName("Redis 동시성 테스트 - 여러 스레드에서 동시 감소")
    void redisConcurrency_ShouldHandleMultipleThreads() throws InterruptedException {
        // Given
        String stockId = "concurrency-test-123";
        Long initialQuantity = 1000L;
        stockRedisRepository.setStock(stockId, initialQuantity);

        // When - 여러 스레드에서 동시에 감소
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                stockRedisRepositoryAdapter.decreaseStock(stockId, 10L);
            });
            threads[i].start();
        }

        // 모든 스레드 완료 대기
        for (Thread thread : threads) {
            thread.join();
        }

        // Then
        Long finalQuantity = stockRedisRepository.getStock(stockId);
        assertThat(finalQuantity).isEqualTo(900L); // 1000 - (10 * 10) = 900
    }
}
