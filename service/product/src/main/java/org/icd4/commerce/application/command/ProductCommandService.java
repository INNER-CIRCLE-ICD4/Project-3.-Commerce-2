package org.icd4.commerce.application.command;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.webapi.dto.ProductResponse;
import org.icd4.commerce.adapter.webapi.dto.event.ProductCreatedEventPayload;
import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.model.ProductMoney;
import org.icd4.commerce.domain.product.request.ProductCategoryUpdateRequest;
import org.icd4.commerce.domain.product.request.ProductCreateRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductCommandService {

    private final ProductRegisterService productRegisterService;
    private final ProductModifierService productModifierService;

    private final ApplicationEventPublisher eventPublisher;

    public ProductResponse create(ProductCreateRequest request) {
        Product product = productRegisterService.create(request);

        eventPublisher.publishEvent(
                ProductCreatedEventPayload.from(product)
        );
        return ProductResponse.fromDomain(productRegisterService.create(request));
    }

    public ProductResponse changeCategory(String productId, ProductCategoryUpdateRequest request) {
        return ProductResponse.fromDomain(
                productModifierService.changeCategory(productId, request.categoryId(), request.sellerId())
        );
    }
    public ProductResponse changeProductPrice(String productId, String sellerId, ProductMoney newPrice) {
        return ProductResponse.fromDomain(productModifierService.changeProductPrice(productId, sellerId, newPrice));
    }
    public ProductResponse activate(String productId, String sellerId) {
        return ProductResponse.fromDomain(productModifierService.activate(productId, sellerId));
    }
    public ProductResponse inactivate(String productId, String sellerId) {
        return ProductResponse.fromDomain(productModifierService.inactivate(productId, sellerId));
    }
    public ProductResponse deleteProduct(String productId, String sellerId) {
        return ProductResponse.fromDomain(productModifierService.deleteProduct(productId, sellerId));
    }
}
