package org.icd4.commerce.adapter.external;

import org.icd4.commerce.domain.cart.ProductId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ProductAggregateServiceAdapter 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductAggregateServiceAdapter 테스트")
class ProductAggregateServiceAdapterTest {
    
    @Mock
    private ProductQueryService productQueryService;
    
    @Mock
    private ProductServiceClient productServiceClient;
    
    @Mock
    private ProductPriceProviderAdapter priceProviderAdapter;
    
    @InjectMocks
    private ProductAggregateServiceAdapter adapter;
    
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
    }
    
    @Test
    @DisplayName("상품 상세 정보 조회 성공")
    void getProductWithDetails_Success() {
        // given
        when(productQueryService.getProduct(testProductId))
            .thenReturn(testProductInfo);
        when(productServiceClient.getAvailableStock(testProductId))
            .thenReturn(100);
        when(priceProviderAdapter.getPrice(testProductId))
            .thenReturn(BigDecimal.valueOf(10000));
        
        // when
        ProductAggregateService.ProductDetails result = adapter.getProductWithDetails(testProductId);
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.product().id()).isEqualTo("PROD-001");
        assertThat(result.product().name()).isEqualTo("테스트 상품");
        assertThat(result.stock().availableQuantity()).isEqualTo(100);
        assertThat(result.stock().status()).isEqualTo(ProductAggregateService.StockInfo.StockStatus.AVAILABLE);
        assertThat(result.price().finalPrice()).isEqualTo(BigDecimal.valueOf(10000));
        assertThat(result.isPurchasable()).isTrue();
        
        verify(productQueryService).getProduct(testProductId);
        verify(productServiceClient).getAvailableStock(testProductId);
        verify(priceProviderAdapter).getPrice(testProductId);
    }
    
    @Test
    @DisplayName("재고 부족 상태 확인")
    void getProductWithDetails_LowStock() {
        // given
        when(productQueryService.getProduct(testProductId))
            .thenReturn(testProductInfo);
        when(productServiceClient.getAvailableStock(testProductId))
            .thenReturn(5);  // 재고 부족
        when(priceProviderAdapter.getPrice(testProductId))
            .thenReturn(BigDecimal.valueOf(10000));
        
        // when
        ProductAggregateService.ProductDetails result = adapter.getProductWithDetails(testProductId);
        
        // then
        assertThat(result.stock().status()).isEqualTo(ProductAggregateService.StockInfo.StockStatus.LOW_STOCK);
        assertThat(result.stock().hasStock()).isTrue();
        assertThat(result.stock().hasStock(10)).isFalse();
    }
    
    @Test
    @DisplayName("품절 상태 확인")
    void getProductWithDetails_OutOfStock() {
        // given
        when(productQueryService.getProduct(testProductId))
            .thenReturn(testProductInfo);
        when(productServiceClient.getAvailableStock(testProductId))
            .thenReturn(0);  // 품절
        when(priceProviderAdapter.getPrice(testProductId))
            .thenReturn(BigDecimal.valueOf(10000));
        
        // when
        ProductAggregateService.ProductDetails result = adapter.getProductWithDetails(testProductId);
        
        // then
        assertThat(result.stock().status()).isEqualTo(ProductAggregateService.StockInfo.StockStatus.OUT_OF_STOCK);
        assertThat(result.stock().hasStock()).isFalse();
        assertThat(result.isPurchasable()).isFalse();
    }
    
    @Test
    @DisplayName("상품을 찾을 수 없는 경우")
    void getProductWithDetails_ProductNotFound() {
        // given
        when(productQueryService.getProduct(testProductId))
            .thenThrow(new ProductNotFoundException(testProductId));
        
        // when & then
        assertThatThrownBy(() -> adapter.getProductWithDetails(testProductId))
            .isInstanceOf(ProductNotFoundException.class);
        
        verify(productQueryService).getProduct(testProductId);
        // 병렬 실행으로 인해 다른 서비스들도 호출될 수 있음
        // verifyNoInteractions(productServiceClient, priceProviderAdapter);
    }
    
    @Test
    @DisplayName("배치로 상품 상세 정보 조회")
    void getProductsWithDetails_Success() {
        // given
        ProductId productId1 = ProductId.of("PROD-001");
        ProductId productId2 = ProductId.of("PROD-002");
        List<ProductId> productIds = List.of(productId1, productId2);
        
        ProductQueryService.ProductInfo productInfo1 = new ProductQueryService.ProductInfo(
            "PROD-001", "상품1", "브랜드1", "설명1", BigDecimal.valueOf(10000), "KRW", 100, true,
            ProductQueryService.ProductInfo.ProductStatus.ON_SALE
        );
        ProductQueryService.ProductInfo productInfo2 = new ProductQueryService.ProductInfo(
            "PROD-002", "상품2", "브랜드2", "설명2", BigDecimal.valueOf(20000), "KRW", 50, true,
            ProductQueryService.ProductInfo.ProductStatus.ON_SALE
        );
        
        when(productQueryService.getProduct(productId1)).thenReturn(productInfo1);
        when(productQueryService.getProduct(productId2)).thenReturn(productInfo2);
        when(productServiceClient.getAvailableStock(any())).thenReturn(100);
        when(priceProviderAdapter.getPrice(any())).thenReturn(BigDecimal.valueOf(15000));
        
        // when
        Map<ProductId, ProductAggregateService.ProductDetails> result = 
            adapter.getProductsWithDetails(productIds);
        
        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsKeys(productId1, productId2);
        assertThat(result.get(productId1).product().name()).isEqualTo("상품1");
        assertThat(result.get(productId2).product().name()).isEqualTo("상품2");
    }
    
    @Test
    @DisplayName("배치 조회 시 일부 실패 처리")
    void getProductsWithDetails_PartialFailure() {
        // given
        ProductId productId1 = ProductId.of("PROD-001");
        ProductId productId2 = ProductId.of("PROD-002");
        List<ProductId> productIds = List.of(productId1, productId2);
        
        when(productQueryService.getProduct(productId1))
            .thenReturn(testProductInfo);
        when(productQueryService.getProduct(productId2))
            .thenThrow(new ProductNotFoundException(productId2));
        when(productServiceClient.getAvailableStock(productId1))
            .thenReturn(100);
        when(priceProviderAdapter.getPrice(productId1))
            .thenReturn(BigDecimal.valueOf(10000));
        
        // when
        Map<ProductId, ProductAggregateService.ProductDetails> result = 
            adapter.getProductsWithDetails(productIds);
        
        // then
        assertThat(result).hasSize(1);
        assertThat(result).containsKey(productId1);
        assertThat(result).doesNotContainKey(productId2);
    }
    
    @Test
    @DisplayName("빈 리스트로 배치 조회")
    void getProductsWithDetails_EmptyList() {
        // when
        Map<ProductId, ProductAggregateService.ProductDetails> result = 
            adapter.getProductsWithDetails(List.of());
        
        // then
        assertThat(result).isEmpty();
        verifyNoInteractions(productQueryService, productServiceClient, priceProviderAdapter);
    }
}