package org.icd4.commerce.command.application.required;

import jakarta.transaction.Transactional;
import org.icd4.commerce.shared.domain.ProductCreateRequest;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public interface ProductCommandRepository {
    String createProductDocument(ProductCreateRequest product) throws IOException;
    String updatePrice(String productId, BigDecimal price, String updateAt) throws IOException;
    String updateStock(String productId, String sku, int stock, String updateAt) throws IOException;
    String updateVariantStatus(String productId, String sku, String variantStatus, String updateAt) throws IOException;
}
