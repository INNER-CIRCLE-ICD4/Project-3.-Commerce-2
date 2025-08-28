package org.icd4.commerce.adapter.external;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.external.exception.ProductServiceException;
import org.icd4.commerce.application.required.common.InventoryReducer;
import org.icd4.commerce.application.required.common.ProductServiceClient;
import org.icd4.commerce.domain.common.ProductId;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class InventoryReducerAdapter implements InventoryReducer {

    private final ProductServiceClient productServiceClient;

    @Override
    public void reduceStock(ProductId productId, int quantity) {

    }
}
