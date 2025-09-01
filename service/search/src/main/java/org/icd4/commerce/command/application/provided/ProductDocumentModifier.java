package org.icd4.commerce.command.application.provided;

import java.io.IOException;
import java.math.BigDecimal;

public interface ProductDocumentModifier {
    String changePrice(String productId, BigDecimal price) throws IOException;
    String changeStock(String productId, String sku, int stock) throws IOException;
    String changeVariantStatus(String productId, String sky, String variantStatus) throws IOException;

    String changeStatus(String productId, String status) throws IOException;
}
