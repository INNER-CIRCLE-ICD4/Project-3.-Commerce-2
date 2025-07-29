package org.icd4.commerce.application.command;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.webapi.dto.ProductResponse;
import org.icd4.commerce.adapter.webapi.dto.event.ProductCreatedEventPayload;
import org.icd4.commerce.domain.product.model.Product;
import org.icd4.commerce.domain.product.request.*;
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
