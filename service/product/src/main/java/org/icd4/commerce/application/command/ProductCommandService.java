package org.icd4.commerce.application.command;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.adapter.webapi.dto.ProductResponse;
import org.icd4.commerce.domain.product.request.ProductCreateRequest;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductCommandService {

    private final ProductRegisterService productRegisterService;
    private final ProductModifierService productModifierService;

    public ProductResponse create(ProductCreateRequest request) {
        // 재고 모듈로 이벤트 발행
        // 검색 모듈로 이벤트 발행
        return ProductResponse.fromDomain(productRegisterService.create(request));
    }
}
