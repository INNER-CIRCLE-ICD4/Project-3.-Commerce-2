package org.icd4.commerce.application.query;

import org.icd4.commerce.adapter.webapi.dto.ProductResponse;
import org.icd4.commerce.adapter.webapi.dto.ProductVariantResponse;
import org.icd4.commerce.domain.ProductFixture;
import org.icd4.commerce.domain.ProductVariantFixture;
import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.model.ProductVariant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.icd4.commerce.domain.ProductFixture.*;
import static org.icd4.commerce.domain.ProductVariantFixture.createProductVariant;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductQueryServiceTest {
    @Mock
    private ProductFinderService productFinderService;

    @Mock
    private ProductVariantFinderService productVariantFinderService;

    @InjectMocks
    private ProductQueryService productQueryService;

    @Test
    @DisplayName("상품 ID로 조회하면 ProductResponse로 변환되어야 한다")
    void findById() {
        // Given
        String productId = "productId";
        Product mockProduct = createProduct(productId);
        when(productFinderService.findById(productId)).thenReturn(mockProduct);

        // When
        ProductResponse response = productQueryService.findById(productId);

        // Then
        assertThat(response.id()).isEqualTo(productId);
        assertThat(response.name()).isEqualTo(mockProduct.getName());
        verify(productFinderService).findById(productId);
    }

    @Test
    @DisplayName("상품의 모든 변형을 조회하면 ProductVariantResponse 리스트로 변환되어야 한다")
    void findAllVariants() {
        // Given
        String productId = "productId";
        Product mockProduct = createProductWithVariant(productId);
        when(productFinderService.findById(productId)).thenReturn(mockProduct);

        // When
        List<ProductVariantResponse> responses = productQueryService.findAllVariants(productId);

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).sku()).isNotBlank();
        verify(productFinderService).findById(productId);
    }

    @Test
    @DisplayName("상품ID와 SKU로 변형 조회하면 적절한 서비스 메서드가 호출되어야 한다")
    void findVariantByProductIdAndSku() {
        // Given
        String productId = "productId";
        String sku = "skuId";
        ProductVariant mockVariant = createProductVariant(sku);
        when(productVariantFinderService.findProductVariantByIdAndSku(productId, sku))
                .thenReturn(mockVariant);

        // When
        ProductVariantResponse response = productQueryService.findVariantByProductIdAndSku(productId, sku);

        // Then
        assertThat(response.sku()).isEqualTo(sku);
        verify(productVariantFinderService).findProductVariantByIdAndSku(productId, sku);
    }
}