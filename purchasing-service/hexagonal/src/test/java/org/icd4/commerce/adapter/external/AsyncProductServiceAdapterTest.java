package org.icd4.commerce.adapter.external;

import org.icd4.commerce.domain.cart.ProductId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AsyncProductServiceAdapter 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AsyncProductServiceAdapter 테스트")
class AsyncProductServiceAdapterTest {
    
    @Mock
    private ProductQueryService productQueryService;
    
    @Mock
    private ProductServiceClient productServiceClient;
    
    @Mock
    private ProductAggregateService productAggregateService;
    
    @Mock
    private ProductPriceProviderAdapter priceProviderAdapter;
    
    @InjectMocks
    private AsyncProductServiceAdapter adapter;
    
    private ProductId testProductId;
    private ProductQueryService.ProductInfo testProductInfo;
    
    @BeforeEach
    void setUp() {
        testProductId = ProductId.of("PROD-001");
        testProductInfo = new ProductQueryService.ProductInfo(
            "PROD-001",
            "테스트 상품",
            "테스트 브랜드",
            "상품 설명",
            BigDecimal.valueOf(10000),
            "KRW",
            100,
            true,
            ProductQueryService.ProductInfo.ProductStatus.ON_SALE
        );
        
        // 테스트용 설정 주입
        ReflectionTestUtils.setField(adapter, "threadPoolSize", 5);
        ReflectionTestUtils.setField(adapter, "timeoutSeconds", 2L);
    }
    
    @Test
    @DisplayName("비동기 단일 상품 조회 성공")
    void getProductAsync_Success() throws Exception {
        // given
        when(productQueryService.getProduct(testProductId))
            .thenReturn(testProductInfo);
        
        // when
        CompletableFuture<ProductQueryService.ProductInfo> future = 
            adapter.getProductAsync(testProductId);
        ProductQueryService.ProductInfo result = future.get(3, TimeUnit.SECONDS);
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("PROD-001");
        assertThat(result.name()).isEqualTo("테스트 상품");
        
        verify(productQueryService).getProduct(testProductId);
    }
    
    @Test
    @DisplayName("비동기 단일 상품 조회 - 예외 처리")
    void getProductAsync_Exception() {
        // given
        when(productQueryService.getProduct(testProductId))
            .thenThrow(new ProductNotFoundException(testProductId));
        
        // when
        CompletableFuture<ProductQueryService.ProductInfo> future = 
            adapter.getProductAsync(testProductId);
        
        // then
        assertThatThrownBy(() -> future.get(3, TimeUnit.SECONDS))
            .hasCauseInstanceOf(ProductNotFoundException.class);
    }
    
    @Test
    @DisplayName("비동기 배치 상품 조회 성공")
    void getProductsAsync_Success() throws Exception {
        // given
        ProductId productId1 = ProductId.of("PROD-001");
        ProductId productId2 = ProductId.of("PROD-002");
        List<ProductId> productIds = List.of(productId1, productId2);
        
        ProductQueryService.ProductInfo productInfo2 = new ProductQueryService.ProductInfo(
            "PROD-002", "상품2", "브랜드2", "설명2", BigDecimal.valueOf(20000), "KRW", 50, true,
            ProductQueryService.ProductInfo.ProductStatus.ON_SALE
        );
        
        when(productQueryService.getProduct(productId1)).thenReturn(testProductInfo);
        when(productQueryService.getProduct(productId2)).thenReturn(productInfo2);
        
        // when
        CompletableFuture<Map<ProductId, ProductQueryService.ProductInfo>> future = 
            adapter.getProductsAsync(productIds);
        Map<ProductId, ProductQueryService.ProductInfo> result = future.get(5, TimeUnit.SECONDS);
        
        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsKeys(productId1, productId2);
        assertThat(result.get(productId1).name()).isEqualTo("테스트 상품");
        assertThat(result.get(productId2).name()).isEqualTo("상품2");
    }
    
    @Test
    @DisplayName("비동기 배치 조회 - 부분 실패 처리")
    void getProductsAsync_PartialFailure() throws Exception {
        // given
        ProductId productId1 = ProductId.of("PROD-001");
        ProductId productId2 = ProductId.of("PROD-002");
        List<ProductId> productIds = List.of(productId1, productId2);
        
        when(productQueryService.getProduct(productId1)).thenReturn(testProductInfo);
        when(productQueryService.getProduct(productId2))
            .thenThrow(new ProductNotFoundException(productId2));
        
        // when
        CompletableFuture<Map<ProductId, ProductQueryService.ProductInfo>> future = 
            adapter.getProductsAsync(productIds);
        Map<ProductId, ProductQueryService.ProductInfo> result = future.get(5, TimeUnit.SECONDS);
        
        // then
        assertThat(result).hasSize(1);
        assertThat(result).containsKey(productId1);
        assertThat(result).doesNotContainKey(productId2);
    }
    
    @Test
    @DisplayName("비동기 상품 상세 정보 조회")
    void getProductDetailsAsync_Success() throws Exception {
        // given
        ProductAggregateService.ProductDetails mockDetails = mock(ProductAggregateService.ProductDetails.class);
        when(productAggregateService.getProductWithDetails(testProductId))
            .thenReturn(mockDetails);
        
        // when
        CompletableFuture<ProductAggregateService.ProductDetails> future = 
            adapter.getProductDetailsAsync(testProductId);
        ProductAggregateService.ProductDetails result = future.get(3, TimeUnit.SECONDS);
        
        // then
        assertThat(result).isEqualTo(mockDetails);
        verify(productAggregateService).getProductWithDetails(testProductId);
    }
    
    @Test
    @DisplayName("비동기 재고 조회")
    void getStockAsync_Success() throws Exception {
        // given
        when(productServiceClient.getAvailableStock(testProductId))
            .thenReturn(100);
        
        // when
        CompletableFuture<Integer> future = adapter.getStockAsync(testProductId);
        Integer result = future.get(3, TimeUnit.SECONDS);
        
        // then
        assertThat(result).isEqualTo(100);
        verify(productServiceClient).getAvailableStock(testProductId);
    }
    
    @Test
    @DisplayName("비동기 가격 조회")
    void getPriceAsync_Success() throws Exception {
        // given
        when(priceProviderAdapter.getPrice(testProductId))
            .thenReturn(BigDecimal.valueOf(15000));
        
        // when
        CompletableFuture<ProductAggregateService.PriceInfo> future = 
            adapter.getPriceAsync(testProductId);
        ProductAggregateService.PriceInfo result = future.get(3, TimeUnit.SECONDS);
        
        // then
        assertThat(result.basePrice()).isEqualTo(BigDecimal.valueOf(15000));
        assertThat(result.finalPrice()).isEqualTo(BigDecimal.valueOf(15000));
        assertThat(result.currency()).isEqualTo("KRW");
        verify(priceProviderAdapter).getPrice(testProductId);
    }
    
    @Test
    @DisplayName("빈 리스트로 비동기 배치 조회")
    void getProductsAsync_EmptyList() throws Exception {
        // when
        CompletableFuture<Map<ProductId, ProductQueryService.ProductInfo>> future = 
            adapter.getProductsAsync(List.of());
        Map<ProductId, ProductQueryService.ProductInfo> result = future.get(1, TimeUnit.SECONDS);
        
        // then
        assertThat(result).isEmpty();
        assertThat(future.isDone()).isTrue();
        verifyNoInteractions(productQueryService);
    }
}