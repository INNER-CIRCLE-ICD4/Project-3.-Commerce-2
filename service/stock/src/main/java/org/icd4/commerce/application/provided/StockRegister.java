package org.icd4.commerce.application.provided;

import org.icd4.commerce.domain.Stock;

public interface StockRegister {
    Stock register(String sku, Long quantity);
    Long increaseQuantity(String sku, Long quantity);
    Long decreaseQuantity(String sku, Long quantity);


}
