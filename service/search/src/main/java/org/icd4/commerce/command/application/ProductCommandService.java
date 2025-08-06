package org.icd4.commerce.command.application;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.shared.domain.ProductCreateRequest;
import org.icd4.commerce.shared.domain.ProductUpdateRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductCommandService {
    private final ProductRegisterService productRegisterService;
    private final ProductModifierService productModifierService;

    public String registerProductIndex(ProductCreateRequest request) {
        return productRegisterService.registerProductIndex(request.toProduct());
    }

    // es에 있는 상품 정보를 전체 업데이트
    public void updateProductIndex(ProductUpdateRequest request) {
        productModifierService.modifyProductInfo(request.toProduct());
    }


    void modifyProductPrice(String productId, BigDecimal newPrice) {
        productModifierService.modifyProductPrice(productId, newPrice);
    }


    void modifyProductStatus(String productId, String status) {
        productModifierService.modifyProductStatus(productId, status);
    }

    void modifyProductVariantPrice(String productId, String sku, BigDecimal newPrice) {
        productModifierService.modifyProductVariantPrice(productId, sku, newPrice);
    }

    void modifyProductVariantStock(String productId, String sku, Integer newStock) {
        productModifierService.modifyProductVariantStock(productId, sku, newStock);
    }

    void modifyProductVariantStatus(String productId, ProductUpdateRequest.ProductVariantDto variants) {
        productModifierService.modifyProductVariantStatus(productId, variants);
    }



    public void deleteProductIndex(String productId) {
        productRegisterService.deleteProductIndex(productId);
    }

}
