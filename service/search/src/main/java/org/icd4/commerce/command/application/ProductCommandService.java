package org.icd4.commerce.command.application;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.shared.domain.Product;
import org.icd4.commerce.shared.domain.ProductCreateRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductCommandService {
    private final ProductRegisterService productRegisterService;
    private final ProductModifierService productModifierService;

    public String registerProductIndex(ProductCreateRequest request) {
        return productRegisterService.registerProductIndex(request.toProduct());
    }

    public void deleteProductIndex(String productId) {
        productRegisterService.deleteProductIndex(productId);
    }
}
