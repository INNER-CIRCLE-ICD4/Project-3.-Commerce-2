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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * ProductQueryServiceAdapter 단위 테스트.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductQueryServiceAdapter 테스트")
class ProductQueryServiceAdapterTest {
    
    @Mock
    private ProductServiceClient productServiceClient;
    
    @Mock
    private ProductServiceRestClient productServiceRestClient;
    
    private ProductQueryServiceAdapter adapter;
    
    private ProductId testProductId;
    private ProductServiceClient.ProductInfo testProductInfo;
    
    @BeforeEach
    void setUp() {
        adapter = new ProductQueryServiceAdapter(productServiceClient, productServiceRestClient);
        testProductId = ProductId.of("PROD-001");
        testProductInfo = new ProductServiceClient.ProductInfo(
            "PROD-001",
            "테스트 상품",
            BigDecimal.valueOf(10000),
            100,
            true
        );
    }
    
    @Test
    @DisplayName("단일 상품 조회 성공")
    void getProduct_Success() {
        // given
        when(productServiceClient.getProduct(testProductId))
            .thenReturn(testProductInfo);
        
        // when
        ProductQueryService.ProductInfo result = adapter.getProduct(testProductId);
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("PROD-001");
        assertThat(result.name()).isEqualTo("테스트 상품");
        assertThat(result.price()).isEqualTo(BigDecimal.valueOf(10000));
        assertThat(result.availableStock()).isEqualTo(100);
        assertThat(result.isActive()).isTrue();
        assertThat(result.status()).isEqualTo(ProductQueryService.ProductInfo.ProductStatus.ON_SALE);
        
        verify(productServiceClient).getProduct(testProductId);
    }
    
    @Test
    @DisplayName("상품을 찾을 수 없는 경우 예외 발생")
    void getProduct_NotFound() {
        // given
        when(productServiceClient.getProduct(testProductId))
            .thenThrow(new ProductNotFoundException(testProductId));
        
        // when & then
        assertThatThrownBy(() -> adapter.getProduct(testProductId))
            .isInstanceOf(ProductNotFoundException.class)
            .hasMessageContaining("PROD-001");
        
        verify(productServiceClient).getProduct(testProductId);
    }
    
    @Test
    @DisplayName("배치 조회 성공")
    void getProducts_Success() {
        // given
        ProductId productId1 = ProductId.of("PROD-001");
        ProductId productId2 = ProductId.of("PROD-002");
        List<ProductId> productIds = List.of(productId1, productId2);
        
        List<ProductServiceClient.ProductInfo> batchResult = List.of(
            new ProductServiceClient.ProductInfo("PROD-001", "상품1", BigDecimal.valueOf(10000), 100, true),
            new ProductServiceClient.ProductInfo("PROD-002", "상품2", BigDecimal.valueOf(20000), 50, true)
        );
        
        when(productServiceRestClient.getProductsBatch(anyList()))
            .thenReturn(batchResult);
        
        // when
        Map<ProductId, ProductQueryService.ProductInfo> result = adapter.getProducts(productIds);
        
        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(productId1).name()).isEqualTo("상품1");
        assertThat(result.get(productId2).name()).isEqualTo("상품2");
        
        verify(productServiceRestClient).getProductsBatch(anyList());
    }
    
    @Test
    @DisplayName("배치 조회 실패 시 개별 조회로 폴백")
    void getProducts_FallbackToIndividual() {
        // given
        ProductId productId1 = ProductId.of("PROD-001");
        ProductId productId2 = ProductId.of("PROD-002");
        List<ProductId> productIds = List.of(productId1, productId2);
        
        when(productServiceRestClient.getProductsBatch(anyList()))
            .thenThrow(new RuntimeException("Batch API failed"));
        
        when(productServiceClient.getProduct(productId1))
            .thenReturn(new ProductServiceClient.ProductInfo("PROD-001", "상품1", BigDecimal.valueOf(10000), 100, true));
        when(productServiceClient.getProduct(productId2))
            .thenReturn(new ProductServiceClient.ProductInfo("PROD-002", "상품2", BigDecimal.valueOf(20000), 50, true));
        
        // when
        Map<ProductId, ProductQueryService.ProductInfo> result = adapter.getProducts(productIds);
        
        // then
        assertThat(result).hasSize(2);
        verify(productServiceRestClient).getProductsBatch(anyList());
        verify(productServiceClient, times(2)).getProduct(any());
    }
    
    @Test
    @DisplayName("findProduct - 상품이 있는 경우")
    void findProduct_Found() {
        // given
        when(productServiceClient.getProduct(testProductId))
            .thenReturn(testProductInfo);
        
        // when
        Optional<ProductQueryService.ProductInfo> result = adapter.findProduct(testProductId);
        
        // then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo("PROD-001");
    }
    
    @Test
    @DisplayName("findProduct - 상품이 없는 경우")
    void findProduct_NotFound() {
        // given
        when(productServiceClient.getProduct(testProductId))
            .thenThrow(new ProductNotFoundException(testProductId));
        
        // when
        Optional<ProductQueryService.ProductInfo> result = adapter.findProduct(testProductId);
        
        // then
        assertThat(result).isEmpty();
    }
    
    @Test
    @DisplayName("빈 리스트로 배치 조회 시 빈 맵 반환")
    void getProducts_EmptyList() {
        // when
        Map<ProductId, ProductQueryService.ProductInfo> result = adapter.getProducts(List.of());
        
        // then
        assertThat(result).isEmpty();
        verifyNoInteractions(productServiceClient, productServiceRestClient);
    }
    
    @Test
    @DisplayName("상품 상태 매핑 - 재고 없음")
    void productStatusMapping_OutOfStock() {
        // given
        ProductServiceClient.ProductInfo outOfStockProduct = 
            new ProductServiceClient.ProductInfo("PROD-001", "상품", BigDecimal.valueOf(10000), 0, true);
        
        when(productServiceClient.getProduct(testProductId))
            .thenReturn(outOfStockProduct);
        
        // when
        ProductQueryService.ProductInfo result = adapter.getProduct(testProductId);
        
        // then
        assertThat(result.status()).isEqualTo(ProductQueryService.ProductInfo.ProductStatus.OUT_OF_STOCK);
    }
    
    @Test
    @DisplayName("상품 상태 매핑 - 비활성")
    void productStatusMapping_Stopped() {
        // given
        ProductServiceClient.ProductInfo inactiveProduct = 
            new ProductServiceClient.ProductInfo("PROD-001", "상품", BigDecimal.valueOf(10000), 100, false);
        
        when(productServiceClient.getProduct(testProductId))
            .thenReturn(inactiveProduct);
        
        // when
        ProductQueryService.ProductInfo result = adapter.getProduct(testProductId);
        
        // then
        assertThat(result.status()).isEqualTo(ProductQueryService.ProductInfo.ProductStatus.STOPPED);
    }
}