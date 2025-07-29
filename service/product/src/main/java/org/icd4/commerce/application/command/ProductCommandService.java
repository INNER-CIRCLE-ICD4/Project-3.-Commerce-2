package org.icd4.commerce.application.command;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.webapi.dto.ProductResponse;
import org.icd4.commerce.adapter.webapi.dto.ProductVariantResponse;
import org.icd4.commerce.adapter.webapi.dto.event.ProductCreatedEventPayload;
import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.model.ProductMoney;
import org.icd4.commerce.domain.product.request.ProductCategoryUpdateRequest;
import org.icd4.commerce.domain.product.request.ProductCreateRequest;
import org.icd4.commerce.domain.product.request.ProductInfoUpdateRequest;
import org.icd4.commerce.domain.product.request.ProductVariantUpdateRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@RequiredArgsConstructor
@Service
@Validated
public class ProductCommandService {

    private final ProductRegisterService productRegisterService;
    private final ProductModifierService productModifierService;

    private final ApplicationEventPublisher eventPublisher;

    public ProductResponse create(ProductCreateRequest request) {
        Product product = productRegisterService.create(request);

        eventPublisher.publishEvent(
                ProductCreatedEventPayload.from(product)
        );
        return ProductResponse.fromDomain(product);
    }

    public ProductResponse changeProductInfo(String productId, String sellerId, ProductInfoUpdateRequest request) {
        Product product = productRegisterService.updateInfo(productId, sellerId, request);
        // 세부에 대한 변경은 이벤트 발행 x?
        return ProductResponse.fromDomain(product);
    }

    public ProductVariantResponse changeProductVariantInfo(String productId, String sku, String sellerId, ProductVariantUpdateRequest request) {
        Product product = productRegisterService.updateVariant(productId, sellerId, sku, request);
        return ProductVariantResponse.fromDomain(product.findVariantBySku(sku));
    }

    public ProductResponse changeCategory(String productId, String sellerId, ProductCategoryUpdateRequest request) {
        return ProductResponse.fromDomain(
                productModifierService.changeCategory(productId, sellerId, request.categoryId())
        );
    }

    //TODO 초희님 구현
    public ProductResponse changeProductPrice(String productId, String sellerId, ProductPriceUpdateRequest request) {
        return ProductResponse.fromDomain(productModifierService.changeProductPrice(productId, sellerId, request.price()));
    }

    //TODO 초희님 구현
    public ProductResponse activate(String productId, String sellerId) {
        return ProductResponse.fromDomain(productModifierService.activate(productId, sellerId));
    }

    //TODO 초희님 구현
    public ProductResponse inactivate(String productId, String sellerId) {
        return ProductResponse.fromDomain(productModifierService.inactivate(productId, sellerId));
    }

    //TODO 초희님 구현
    public ProductResponse deleteProduct(String productId, String sellerId) {
        return ProductResponse.fromDomain(productModifierService.deleteProduct(productId, sellerId));
    }
}
