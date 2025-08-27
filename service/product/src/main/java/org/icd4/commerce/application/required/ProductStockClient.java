package org.icd4.commerce.application.required;

public interface ProductStockClient {
    String updateStock(String productId, Long quantity);
}
