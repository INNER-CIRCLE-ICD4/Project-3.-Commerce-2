package org.icd4.commerce.command.application.provided;

import lombok.RequiredArgsConstructor;
import org.icd4.commerce.command.application.required.ProductCommandRepository;
import org.icd4.commerce.query.application.provided.ProductDocumentFinder;
import org.icd4.commerce.shared.domain.Product;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class ProductDocumentModifierService implements ProductDocumentModifier {
    private final ProductDocumentFinder productDocumentFinder;
    private final ProductCommandRepository productCommandRepository;

    @Override
    public String changePrice(String productId, BigDecimal price) throws IOException {
        Product product = productDocumentFinder.findById(productId);
        // product validation()
        String testTime = LocalDateTime.now(ZoneOffset.UTC).toString();
        return productCommandRepository.updatePrice(productId, price, testTime);
    }

    @Override
    public String changeStock(String productId, String sku, int stock) throws IOException {
        Product product = productDocumentFinder.findById(productId);
        // product validation()
        String testTime = LocalDateTime.now(ZoneOffset.UTC).toString();
        return productCommandRepository.updateStock(productId, sku, stock, testTime);
    }


    @Override
    public String changeVariantStatus(String productId, String sku, String variantStatus) throws IOException {
        Product product = productDocumentFinder.findById(productId);
        // product validation()
        String testTime = LocalDateTime.now(ZoneOffset.UTC).toString();
        return productCommandRepository.updateVariantStatus(productId, sku, variantStatus, testTime);
    }
}
