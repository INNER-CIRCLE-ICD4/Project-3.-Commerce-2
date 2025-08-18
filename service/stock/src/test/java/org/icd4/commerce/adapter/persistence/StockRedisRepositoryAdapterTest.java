
package org.icd4.commerce.adapter.persistence;

import org.icd4.commerce.domain.Stock;
import org.icd4.commerce.domain.StockStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockRedisRepositoryAdapterTest {

    @Mock
    private StockRedisRepository stockRedisRepository;

    @InjectMocks
    private StockRedisRepositoryAdapter stockRedisRepositoryAdapter;

    private Stock testStock;

    @BeforeEach
    void setUp() {
        testStock = Stock.register("test-product-123", 100L);
    }

    @Test
    @DisplayName("save - Redis에 재고 수량 저장")
    void save_ShouldSaveStockQuantityToRedis() {
        // Given
        when(stockRedisRepository.setStock(eq(testStock.getId()), eq(testStock.getQuantity())))
                .thenReturn(testStock.getQuantity());

        // When
        Stock savedStock = stockRedisRepositoryAdapter.save(testStock);

        // Then
        assertThat(savedStock).isEqualTo(testStock);
        verify(stockRedisRepository, times(1)).setStock(testStock.getId(), testStock.getQuantity());
    }

    @Test
    @DisplayName("findById - Redis에서 재고 조회 성공")
    void findById_ShouldReturnStockFromRedis() {
        // Given
        String stockId = "test-stock-123";
        Long quantity = 50L;
        when(stockRedisRepository.getStock(stockId)).thenReturn(quantity);

        // When
        Optional<Stock> foundStock = stockRedisRepositoryAdapter.findById(stockId);

        // Then
        assertThat(foundStock).isPresent();
        Stock stock = foundStock.get();
        assertThat(stock.getId()).isEqualTo(stockId);
        assertThat(stock.getQuantity()).isEqualTo(quantity);
        assertThat(stock.getProductId()).isEqualTo("unknown");
        assertThat(stock.getStockStatus()).isEqualTo(StockStatus.AVAILABLE);
        verify(stockRedisRepository, times(1)).getStock(stockId);
    }

    @Test
    @DisplayName("findById - Redis에 재고가 없는 경우")
    void findById_ShouldReturnEmptyWhenStockNotFound() {
        // Given
        String stockId = "non-existent-stock";
        when(stockRedisRepository.getStock(stockId)).thenReturn(null);

        // When
        Optional<Stock> foundStock = stockRedisRepositoryAdapter.findById(stockId);

        // Then
        assertThat(foundStock).isEmpty();
        verify(stockRedisRepository, times(1)).getStock(stockId);
    }

    @Test
    @DisplayName("findById - 재고 수량이 0인 경우 OUT_OF_STOCK 상태")
    void findById_ShouldSetOutOfStockStatusWhenQuantityIsZero() {
        // Given
        String stockId = "test-stock-123";
        Long quantity = 0L;
        when(stockRedisRepository.getStock(stockId)).thenReturn(quantity);

        // When
        Optional<Stock> foundStock = stockRedisRepositoryAdapter.findById(stockId);

        // Then
        assertThat(foundStock).isPresent();
        Stock stock = foundStock.get();
        assertThat(stock.getQuantity()).isEqualTo(0L);
        assertThat(stock.getStockStatus()).isEqualTo(StockStatus.OUT_OF_STOCK);
    }

    @Test
    @DisplayName("increaseStock - Redis에서 재고 증가 성공")
    void increaseStock_ShouldIncreaseStockInRedis() {
        // Given
        String stockId = "test-stock-123";
        Long currentQuantity = 100L;
        Long increaseQuantity = 50L;
        Long newQuantity = currentQuantity + increaseQuantity;

        when(stockRedisRepository.getStock(stockId)).thenReturn(currentQuantity);
        when(stockRedisRepository.setStock(stockId, newQuantity)).thenReturn(newQuantity);

        // When
        int result = stockRedisRepositoryAdapter.increaseStock(stockId, increaseQuantity);

        // Then
        assertThat(result).isEqualTo(1);
        verify(stockRedisRepository, times(1)).getStock(stockId);
        verify(stockRedisRepository, times(1)).setStock(stockId, newQuantity);
    }

    @Test
    @DisplayName("increaseStock - Redis에 재고가 없는 경우 실패")
    void increaseStock_ShouldFailWhenStockNotFound() {
        // Given
        String stockId = "non-existent-stock";
        Long increaseQuantity = 50L;
        when(stockRedisRepository.getStock(stockId)).thenReturn(null);

        // When
        int result = stockRedisRepositoryAdapter.increaseStock(stockId, increaseQuantity);

        // Then
        assertThat(result).isEqualTo(0);
        verify(stockRedisRepository, times(1)).getStock(stockId);
        verify(stockRedisRepository, never()).setStock(anyString(), any(Long.class));
    }

    @Test
    @DisplayName("decreaseStock - Redis에서 재고 감소 성공")
    void decreaseStock_ShouldDecreaseStockInRedis() {
        // Given
        String stockId = "test-stock-123";
        Long quantity = 30L;
        Long newQuantity = 70L; // 100 - 30

        when(stockRedisRepository.decreaseStock(stockId, quantity)).thenReturn(newQuantity);

        // When
        int result = stockRedisRepositoryAdapter.decreaseStock(stockId, quantity);

        // Then
        assertThat(result).isEqualTo(1);
        verify(stockRedisRepository, times(1)).decreaseStock(stockId, quantity);
        verify(stockRedisRepository, never()).setStock(anyString(), any(Long.class));
    }

    @Test
    @DisplayName("decreaseStock - 재고 부족으로 인한 실패")
    void decreaseStock_ShouldFailWhenInsufficientStock() {
        // Given
        String stockId = "test-stock-123";
        Long quantity = 150L; // 현재 재고보다 많은 수량
        Long newQuantity = -50L; // 음수가 됨 (재고 부족)

        when(stockRedisRepository.decreaseStock(stockId, quantity)).thenReturn(newQuantity);
        when(stockRedisRepository.setStock(stockId, newQuantity + quantity)).thenReturn(100L);

        // When
        int result = stockRedisRepositoryAdapter.decreaseStock(stockId, quantity);

        // Then
        assertThat(result).isEqualTo(0);
        verify(stockRedisRepository, times(1)).decreaseStock(stockId, quantity);
        verify(stockRedisRepository, times(1)).setStock(stockId, newQuantity + quantity); // 원래 수량으로 되돌림
    }

    @Test
    @DisplayName("decreaseStock - 정확히 남은 수량만큼 감소")
    void decreaseStock_ShouldSucceedWhenExactRemainingQuantity() {
        // Given
        String stockId = "test-stock-123";
        Long quantity = 100L; // 정확히 남은 수량
        Long newQuantity = 0L; // 정확히 0이 됨

        when(stockRedisRepository.decreaseStock(stockId, quantity)).thenReturn(newQuantity);

        // When
        int result = stockRedisRepositoryAdapter.decreaseStock(stockId, quantity);

        // Then
        assertThat(result).isEqualTo(1);
        verify(stockRedisRepository, times(1)).decreaseStock(stockId, quantity);
        verify(stockRedisRepository, never()).setStock(anyString(), any(Long.class));
    }

    @Test
    @DisplayName("decreaseStock - 0 수량 감소")
    void decreaseStock_ShouldSucceedWhenZeroQuantity() {
        // Given
        String stockId = "test-stock-123";
        Long quantity = 0L;
        Long newQuantity = 100L; // 변화 없음

        when(stockRedisRepository.decreaseStock(stockId, quantity)).thenReturn(newQuantity);

        // When
        int result = stockRedisRepositoryAdapter.decreaseStock(stockId, quantity);

        // Then
        assertThat(result).isEqualTo(1);
        verify(stockRedisRepository, times(1)).decreaseStock(stockId, quantity);
    }

    @Test
    @DisplayName("Redis 데이터로부터 Stock 객체 생성 - 기본값 확인")
    void createStockFromRedis_ShouldCreateStockWithDefaultValues() {
        // Given
        String stockId = "test-stock-123";
        Long quantity = 75L;
        when(stockRedisRepository.getStock(stockId)).thenReturn(quantity);

        // When
        Optional<Stock> foundStock = stockRedisRepositoryAdapter.findById(stockId);

        // Then
        assertThat(foundStock).isPresent();
        Stock stock = foundStock.get();

        // 기본값 확인
        assertThat(stock.getId()).isEqualTo(stockId);
        assertThat(stock.getQuantity()).isEqualTo(quantity);
        assertThat(stock.getProductId()).isEqualTo("unknown"); // Redis에는 productId가 없으므로 기본값
        assertThat(stock.getStockStatus()).isEqualTo(StockStatus.AVAILABLE); // 수량 > 0이므로 AVAILABLE
        assertThat(stock.getCreatedAt()).isNotNull();
        assertThat(stock.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Redis 데이터로부터 Stock 객체 생성 - 0 수량일 때 OUT_OF_STOCK")
    void createStockFromRedis_ShouldSetOutOfStockWhenQuantityIsZero() {
        // Given
        String stockId = "test-stock-123";
        Long quantity = 0L;
        when(stockRedisRepository.getStock(stockId)).thenReturn(quantity);

        // When
        Optional<Stock> foundStock = stockRedisRepositoryAdapter.findById(stockId);

        // Then
        assertThat(foundStock).isPresent();
        Stock stock = foundStock.get();
        assertThat(stock.getQuantity()).isEqualTo(0L);
        assertThat(stock.getStockStatus()).isEqualTo(StockStatus.OUT_OF_STOCK);
    }

    @Test
    @DisplayName("Redis 데이터로부터 Stock 객체 생성 - 음수 수량일 때 OUT_OF_STOCK")
    void createStockFromRedis_ShouldSetOutOfStockWhenQuantityIsNegative() {
        // Given
        String stockId = "test-stock-123";
        Long quantity = -10L; // 음수 수량
        when(stockRedisRepository.getStock(stockId)).thenReturn(quantity);

        // When
        Optional<Stock> foundStock = stockRedisRepositoryAdapter.findById(stockId);

        // Then
        assertThat(foundStock).isPresent();
        Stock stock = foundStock.get();
        assertThat(stock.getQuantity()).isEqualTo(-10L);
        assertThat(stock.getStockStatus()).isEqualTo(StockStatus.OUT_OF_STOCK);
    }

    @Test
    @DisplayName("Redis 원자적 연산 테스트 - 여러 번 감소")
    void decreaseStock_ShouldUseRedisAtomicOperation() {
        // Given
        String stockId = "test-stock-123";

        // Redis의 원자적 decrement 연산 시뮬레이션
        when(stockRedisRepository.decreaseStock(stockId, 30L)).thenReturn(70L);
        when(stockRedisRepository.decreaseStock(stockId, 40L)).thenReturn(30L);
        when(stockRedisRepository.decreaseStock(stockId, 20L)).thenReturn(10L);

        // When
        int result1 = stockRedisRepositoryAdapter.decreaseStock(stockId, 30L);
        int result2 = stockRedisRepositoryAdapter.decreaseStock(stockId, 40L);
        int result3 = stockRedisRepositoryAdapter.decreaseStock(stockId, 20L);

        // Then
        assertThat(result1).isEqualTo(1);
        assertThat(result2).isEqualTo(1);
        assertThat(result3).isEqualTo(1);

        // Redis의 원자적 연산이 각각 호출되었는지 확인
        verify(stockRedisRepository, times(1)).decreaseStock(stockId, 30L);
        verify(stockRedisRepository, times(1)).decreaseStock(stockId, 40L);
        verify(stockRedisRepository, times(1)).decreaseStock(stockId, 20L);
    }

    @Test
    @DisplayName("Redis 원자적 연산 테스트 - 재고 부족 시 롤백")
    void decreaseStock_ShouldRollbackWhenInsufficientStock() {
        // Given
        String stockId = "test-stock-123";

        // 첫 번째 감소: 성공
        when(stockRedisRepository.decreaseStock(stockId, 60L)).thenReturn(40L);
        // 두 번째 감소: 실패 (재고 부족)
        when(stockRedisRepository.decreaseStock(stockId, 50L)).thenReturn(-10L);
        when(stockRedisRepository.setStock(stockId, -10L + 50L)).thenReturn(40L);

        // When
        int result1 = stockRedisRepositoryAdapter.decreaseStock(stockId, 60L);
        int result2 = stockRedisRepositoryAdapter.decreaseStock(stockId, 50L);

        // Then
        assertThat(result1).isEqualTo(1);
        assertThat(result2).isEqualTo(0);

        // 성공한 감소
        verify(stockRedisRepository, times(1)).decreaseStock(stockId, 60L);
        // 실패한 감소 (롤백 포함)
        verify(stockRedisRepository, times(1)).decreaseStock(stockId, 50L);
        verify(stockRedisRepository, times(1)).setStock(stockId, 40L); // 롤백
    }

    @Test
    @DisplayName("Redis 원자적 연산 테스트 - 정확히 0이 되는 경우")
    void decreaseStock_ShouldSucceedWhenResultIsExactlyZero() {
        // Given
        String stockId = "test-stock-123";
        Long quantity = 100L;
        Long newQuantity = 0L; // 정확히 0

        when(stockRedisRepository.decreaseStock(stockId, quantity)).thenReturn(newQuantity);

        // When
        int result = stockRedisRepositoryAdapter.decreaseStock(stockId, quantity);

        // Then
        assertThat(result).isEqualTo(1);
        verify(stockRedisRepository, times(1)).decreaseStock(stockId, quantity);
        verify(stockRedisRepository, never()).setStock(anyString(), any(Long.class));
    }

    @Test
    @DisplayName("Redis 원자적 연산 테스트 - 음수 결과 시 롤백")
    void decreaseStock_ShouldRollbackWhenResultIsNegative() {
        // Given
        String stockId = "test-stock-123";
        Long quantity = 120L;
        Long newQuantity = -20L; // 음수 결과

        when(stockRedisRepository.decreaseStock(stockId, quantity)).thenReturn(newQuantity);
        when(stockRedisRepository.setStock(stockId, newQuantity + quantity)).thenReturn(100L);

        // When
        int result = stockRedisRepositoryAdapter.decreaseStock(stockId, quantity);

        // Then
        assertThat(result).isEqualTo(0);
        verify(stockRedisRepository, times(1)).decreaseStock(stockId, quantity);
        verify(stockRedisRepository, times(1)).setStock(stockId, 100L); // 롤백
    }
}