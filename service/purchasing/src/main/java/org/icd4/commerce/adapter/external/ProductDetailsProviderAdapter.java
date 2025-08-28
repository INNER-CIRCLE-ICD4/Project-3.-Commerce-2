package org.icd4.commerce.adapter.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.icd4.commerce.adapter.external.exception.ProductNotFoundException;
import org.icd4.commerce.application.provided.common.ProductDetailsProvider;
import org.icd4.commerce.application.required.common.ProductServiceClient;
import org.icd4.commerce.domain.common.ProductId;
import org.icd4.commerce.domain.common.ProductPriceProvider;
import org.icd4.commerce.domain.common.StockKeepingUnit;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * ProductPriceProvider와 ProductDetailsProvider의 어댑터 구현체.
 * 
 * <p>도메인의 ProductPriceProvider와 application의 ProductDetailsProvider 인터페이스를 구현하여
 * 외부 상품 서비스로부터 상품명 및 가격 정보를 조회합니다.</p>
 * 
 * <p>이 어댑터는 헥사고날 아키텍처의 Secondary(Driven) Adapter로,
 * 도메인이 외부 시스템에 의존하지 않도록 격리합니다.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductDetailsProviderAdapter implements ProductPriceProvider, ProductDetailsProvider {
    
    private final ProductServiceClient productServiceClient;
    
    @Override
    public BigDecimal getPrice(ProductId productId, StockKeepingUnit sku) {
        if (productId == null) {
            throw new NullPointerException("ProductId cannot be null");
        }
        
        try {
            log.debug("Getting price for product: {}", productId);
            
            ProductServiceClient.ProductInfo product = 
                productServiceClient.getProduct(productId, sku);
            
            if (!product.isActive()) {
                throw new IllegalArgumentException(
                    "Product is not active: " + productId.value()
                );
            }
            
            log.debug("Product price retrieved: {} -> {}", 
                productId, product.price());
            
            return product.price();
            
        } catch (ProductNotFoundException e) {
            throw new IllegalArgumentException(
                "Product not found: " + productId.value(), e
            );
        }
    }

    @Override
    public ProductDetails getProductInfo(ProductId productId, StockKeepingUnit sku) {
        ProductServiceClient.ProductInfo product = productServiceClient.getProduct(productId, sku);

        if (!product.isActive()) {
            throw new IllegalArgumentException("비활성 상품입니다: " + productId.value());
        }

        return new ProductDetails(
                product.name(),
                product.price(),
                true
        );
    }
}