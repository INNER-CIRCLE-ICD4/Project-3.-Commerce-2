package org.icd4.commerce.application.command;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.webapi.dto.ProductResponse;
import org.icd4.commerce.adapter.webapi.dto.event.ProductCreatedEventPayload;
import org.icd4.commerce.domain.product.model.Product;
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
}
