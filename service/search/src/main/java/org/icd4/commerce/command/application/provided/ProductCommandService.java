package org.icd4.commerce.command.application.provided;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.shared.domain.ProductCreateRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductCommandService {
    private final ProductDocumentRegisterService productDocumentRegisterService;
    private final ProductDocumentModifierService productDocumentModifierService;

    public String create(ProductCreateRequest request) throws IOException {
        return productDocumentRegisterService.create(request);
    }

    public void delete(String productId) {
        productDocumentRegisterService.delete(productId);
    }

    public String updatePrice(String productId, int price) throws IOException {
        BigDecimal changePrice = BigDecimal.valueOf(price);
        return productDocumentModifierService.changePrice(productId, changePrice);
    }

    public String updateStock(String productId, String sku, int stock) throws IOException {
        return productDocumentModifierService.changeStock(productId, sku, stock);
    }

    public String updateVariantStatus(String productId, String sku, String variantStatus) throws IOException {
        return productDocumentModifierService.changeVariantStatus(productId, sku, variantStatus);
    }

    public String updateStatus(String productId, String status) throws IOException {
        return productDocumentModifierService.changeStatus(productId, status);
    }
}
