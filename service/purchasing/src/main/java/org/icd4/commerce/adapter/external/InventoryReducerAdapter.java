package org.icd4.commerce.adapter.external;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.external.exception.NotEnoughStockException;
import org.icd4.commerce.adapter.external.exception.ProductNotFoundException;
import org.icd4.commerce.adapter.external.exception.ProductServiceException;
import org.icd4.commerce.application.required.common.InventoryReducer;
import org.icd4.commerce.domain.common.ProductId;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class InventoryReducerAdapter implements InventoryReducer {

    private final ProductServiceClient productServiceClient;

    @Override
    public void reduceStock(ProductId productId, int quantity) {
        if(productId == null){
            throw new NullPointerException("상품ID는 null일 수 없습니다.");
        }

        try {
            productServiceClient.reduceStock(productId, quantity);
        } catch (RestClientException e) {
            throw new ProductServiceException("서비스 통신 오류입니다.", e);
        }
    }
}
