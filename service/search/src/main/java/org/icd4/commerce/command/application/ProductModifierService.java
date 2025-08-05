package org.icd4.commerce.command.application;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.command.application.provide.ProductModifier;
import org.icd4.commerce.command.application.required.ProductCommandCustomRepository;
import org.icd4.commerce.shared.domain.Product;
import org.icd4.commerce.shared.domain.ProductUpdateRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductModifierService implements ProductModifier {

    private final ProductCommandCustomRepository productCustomRepository;
    @Override
    public void modifyProductInfo(Product product) {
        productCustomRepository.updateProduct(product);
    }

    @Override
    public void modifyProductPrice(String productId, BigDecimal newPrice) {
        productCustomRepository.updateProductPrice(productId, newPrice);
    }

    @Override
    public void modifyProductStatus(String productId, String status) {
        productCustomRepository.updateProductStatus(productId, status);
    }

    @Override
    public void modifyProductVariantStatus(String productId, ProductUpdateRequest.ProductVariantDto variants) {
        productCustomRepository.updateProductStatus(productId, variants.status());
    }

    @Override
    public void modifyProductVariantPrice(String productId, String sku, BigDecimal newPrice) {
        productCustomRepository.updateProductVariantPrice(productId, sku, newPrice);
    }

    @Override
    public void modifyProductVariantStock(String productId, String sku, Integer newStock) {
       productCustomRepository.updateProductVariantStock(productId, sku, newStock);
    }
}
